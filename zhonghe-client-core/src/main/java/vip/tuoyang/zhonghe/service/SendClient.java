package vip.tuoyang.zhonghe.service;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import lombok.extern.slf4j.Slf4j;
import vip.tuoyang.base.exception.BizException;
import vip.tuoyang.zhonghe.bean.ResultInternal;
import vip.tuoyang.zhonghe.config.ZhongHeConfig;
import vip.tuoyang.zhonghe.constants.CmdEnum;
import vip.tuoyang.zhonghe.service.handler.MyChannelInitializer;
import vip.tuoyang.zhonghe.support.SyncResultSupport;
import vip.tuoyang.zhonghe.utils.ConvertCode;
import vip.tuoyang.zhonghe.utils.ServiceUtils;

import java.net.InetSocketAddress;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author AlanSun
 * @date 2021/8/24 15:37
 */
@Slf4j
public class SendClient {
    private final AtomicInteger atomicInteger = new AtomicInteger(1);
    private Channel channel;
    private static ZhongHeConfig zhongHeConfig;

    private static volatile SendClient sendClient;

    private static InetSocketAddress inetSocketAddress;

    private ReentrantLock lock = new ReentrantLock();

    private SendClient() {
        startListener();
    }

    public static void init(ZhongHeConfig zhongHeConfig1) {
        zhongHeConfig = zhongHeConfig1;
    }

    public static SendClient getSingleton() {
        if (sendClient == null) {
            synchronized (SendClient.class) {
                if (sendClient == null) {
                    sendClient = new SendClient();
                    inetSocketAddress = new InetSocketAddress(zhongHeConfig.getMiddleWareIp(), zhongHeConfig.getMiddleWarePort());
                }
            }
        }
        return sendClient;
    }

    private final ExecutorService executorService = new ThreadPoolExecutor(4, 4, 0, TimeUnit.SECONDS, new ArrayBlockingQueue<>(50), r -> {
        Thread thread = new Thread(r);
        thread.setName("zhonghe-client-channel-start");
        thread.setDaemon(false);
        return thread;
    });

    /**
     * 启动客户端监听
     */
    private void startListener() {
        executorService.execute(() -> {
            EventLoopGroup group = new NioEventLoopGroup();
            try {
                Bootstrap b = new Bootstrap();
                b.group(group)
                        .channel(NioDatagramChannel.class)
                        .handler(new MyChannelInitializer());
                channel = b.bind(zhongHeConfig.getLocalBindPort()).sync().channel();
                channel.closeFuture().await();
            } catch (Exception e) {
                log.error("启动中河客户端失败", e);
            } finally {
                group.shutdownGracefully();
            }
        });
    }

    /**
     * 获取 channel
     *
     * @return {@link Channel}
     */
    private Channel getChannel() {
        while (channel == null) {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return channel;
    }

    /**
     * 发送指令
     *
     * @param cmdEnum 指令
     * @param para    指令参数
     * @param content 16 字节之后的参数
     */
    public ResultInternal send(CmdEnum cmdEnum, String para, String content) {
        final int holdCount = lock.getHoldCount();
        if (holdCount > 20) {
            throw new BizException("系统繁忙");
        }
        lock.lock();
        try {
            SyncResultSupport.cmdResultCountDownMap.get(cmdEnum).reset();
            StringBuilder sb = new StringBuilder();
            // 0 1 2 固定
            sb.append("FEE0A7");
            // 3 type 固定
            sb.append("8A");
            // 4 5 6 7 deviceId, init 时可以传0
            sb.append(ServiceUtils.changeOrder(zhongHeConfig.getDeviceId(), 2));
            // 8 9 sn 帧数
            sb.append(ServiceUtils.changeOrder(ConvertCode.intToHexString(atomicInteger.get(), 2), 2));
            // 12 cmd
            sb.append(cmdEnum.getValue());
            // 13 para
            sb.append(para);
            // 14 accept
            sb.append("00");
            // 10 11 len
            String lenLittle = "0000";
            if (content != null) {
                lenLittle = ServiceUtils.changeOrder(ConvertCode.intToHexString(content.length() / 2, 2), 2);
            }
            sb.insert(20, lenLittle);
            // 15 chkSum
            sb.insert(30, ConvertCode.intToHexString(ServiceUtils.computeChkSum(sb.substring(0, 30)), 1));
            // 16 字节之后的数据
            if (content != null) {
                sb.append(content);
            }

            log.info("cmd: [{}], para: [{}], 发送: [{}]", cmdEnum, para, sb.toString());
            getChannel().writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer(Objects.requireNonNull(ConvertCode.hexString2Bytes(sb.toString()))), inetSocketAddress));
            try {
                SyncResultSupport.cmdResultCountDownMap.get(cmdEnum).await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ResultInternal zhongHeResult = SyncResultSupport.cmdResultMap.get(cmdEnum);
            if (zhongHeResult == null) {
                log.info("cmd: [{}], para: [{}], 收到: [{}]", cmdEnum, para, "10秒超时");
                zhongHeResult = new ResultInternal();
                zhongHeResult.setSuccess(false);
                zhongHeResult.setErrorMsg("超时");
            } else {
                log.info("cmd: [{}], para: [{}], 收到: [{}]", cmdEnum, para, zhongHeResult.getOriginalData());
            }

            return zhongHeResult;
        } finally {
            lock.unlock();
            SyncResultSupport.cmdResultMap.remove(cmdEnum);
        }
    }

    /**
     * 发送指令
     *
     * @param cmd     指令
     * @param para    指令参数
     * @param content 16 字节之后的参数
     */
    public void sendAsync(String cmd, String para, String content) {
        StringBuilder sb = new StringBuilder();
        // 0 1 2 固定
        sb.append("FEE0A7");
        // 3 type 固定
        sb.append("8A");
        // 4 5 6 7 deviceId, init 时可以传0
        sb.append(ServiceUtils.changeOrder(zhongHeConfig.getDeviceId(), 2));
        // 8 9 sn 帧数
        sb.append(ServiceUtils.changeOrder(ConvertCode.intToHexString(atomicInteger.get(), 2), 2));
        // 12 cmd
        sb.append(cmd);
        // 13 para
        sb.append(para);
        // 14 accept
        sb.append("00");
        // 10 11 len
        String lenLittle = "0000";
        if (content != null) {
            lenLittle = ServiceUtils.changeOrder(ConvertCode.intToHexString(content.length() / 2, 2), 2);
        }
        sb.insert(20, lenLittle);
        // 15 chkSum
        sb.insert(30, ConvertCode.intToHexString(ServiceUtils.computeChkSum(sb.substring(0, 30)), 1));
        // 16 字节之后的数据
        if (content != null) {
            sb.append(content);
        }

        log.info("cmd: [{}], para: [{}], 发送: [{}]", cmd, para, sb.toString());
        getChannel().writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer(Objects.requireNonNull(ConvertCode.hexString2Bytes(sb.toString()))), inetSocketAddress));
    }
}
