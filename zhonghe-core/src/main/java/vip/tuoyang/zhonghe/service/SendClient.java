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
import vip.tuoyang.base.util.ThreadUtils;
import vip.tuoyang.zhonghe.bean.ResultInternal;
import vip.tuoyang.zhonghe.config.ZhongHeConfig;
import vip.tuoyang.zhonghe.config.ZhongHeSystemProperties;
import vip.tuoyang.zhonghe.constants.CmdEnum;
import vip.tuoyang.zhonghe.exception.TimeOutException;
import vip.tuoyang.zhonghe.service.handler.MyChannelInitializer;
import vip.tuoyang.zhonghe.support.CountDownLatch2;
import vip.tuoyang.zhonghe.support.SyncResultSupport;
import vip.tuoyang.zhonghe.support.ZhongHeCallback;
import vip.tuoyang.zhonghe.utils.ConvertCode;
import vip.tuoyang.zhonghe.utils.ServiceUtils;

import java.net.InetSocketAddress;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author AlanSun
 * @date 2021/8/24 15:37
 */
@Slf4j
public class SendClient {
    private final AtomicInteger atomicInteger = new AtomicInteger(1);
    private volatile Channel channel;
    private final ZhongHeConfig zhongHeConfig;
    private final InetSocketAddress inetSocketAddress;

    private final String label;

    private final ZhongHeCallback callback;

    private EventLoopGroup group;

    private final ExecutorService executorService;

    private final CountDownLatch2 countDownLatch2 = new CountDownLatch2(1);

    public SendClient(ZhongHeConfig zhongHeConfig, String label, ZhongHeCallback callback) {
        this.zhongHeConfig = zhongHeConfig;
        inetSocketAddress = new InetSocketAddress(zhongHeConfig.getMiddleWareIp(), zhongHeConfig.getMiddleWarePort());
        this.label = label;
        this.callback = callback;
        executorService = new ThreadPoolExecutor(4, 4, 0, TimeUnit.SECONDS, new ArrayBlockingQueue<>(50), r -> {
            Thread thread = new Thread(r);
            thread.setName("zhonghe-client-channel-start_" + label);
            thread.setDaemon(true);
            return thread;
        });
    }


    /**
     * 启动客户端监听
     */
    private void startListener() {
        executorService.execute(() -> {
            group = new NioEventLoopGroup();
            try {
                Bootstrap b = new Bootstrap();
                b.group(group)
                        .channel(NioDatagramChannel.class)
                        .handler(new MyChannelInitializer(label, callback, this));
                channel = b.bind(zhongHeConfig.getLocalBindPort()).sync().channel();
                countDownLatch2.countDown();
                channel.closeFuture().await();
            } catch (Exception e) {
                // Address already in use: bind
                log.error("启动中河客户端失败", e);
                countDownLatch2.countDown();
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
    public Channel getChannel() {
        int retryCount = 10;
        while (channel == null && retryCount > 0) {
            synchronized (atomicInteger) {
                if (channel == null) {
                    this.startListener();
                    try {
                        countDownLatch2.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        countDownLatch2.reset();
                    }
                }
            }
            retryCount--;
        }

        if (channel == null) {
            throw new BizException("获取 channel 失败");
        }

        if (!channel.isActive()) {
            group.shutdownGracefully();
            channel = null;
            this.getChannel();
        }

        return channel;
    }

    public void close() {
        if (channel != null && channel.isActive()) {
            channel.close();
        }

        if (group != null) {
            group.shutdownGracefully();
        }

        ThreadUtils.shutdownAndAwaitTermination(executorService);
    }

    /**
     * 发送指令
     *
     * @param cmdEnum 指令
     * @param para    指令参数
     * @param content 16 字节之后的参数
     */
    public ResultInternal send(CmdEnum cmdEnum, String para, String content) {
        try {
            SyncResultSupport.labelResultCountDownMap.get(label).reset();
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
                if (cmdEnum.equals(CmdEnum.CLOSE)) {
                    SyncResultSupport.labelResultCountDownMap.get(label).await(3, TimeUnit.SECONDS);
                } else {
                    SyncResultSupport.labelResultCountDownMap.get(label).await(ZhongHeSystemProperties.timeout, TimeUnit.SECONDS);
                }
            } catch (InterruptedException e) {
                log.error("终端异常", e);
            }
            ResultInternal resultInternal = SyncResultSupport.labelResultInternal.get(label);
            if (resultInternal == null) {
                log.info("cmd: [{}], para: [{}], 收到: [{}]", cmdEnum, para, "10秒超时");
                throw new TimeOutException();
            }

            return resultInternal;
        } finally {
            SyncResultSupport.labelResultInternal.remove(label);
        }
    }

    /**
     * 发送指令
     *
     * @param cmdEnum 指令
     * @param para    指令参数
     * @param content 16 字节之后的参数
     */
    public void sendAsync(CmdEnum cmdEnum, String para, long sn, String content) {
        StringBuilder sb = new StringBuilder();
        // 0 1 2 固定
        sb.append("FEE0A7");
        // 3 type 固定
        sb.append("8A");
        // 4 5 6 7 deviceId, init 时可以传0
        sb.append(ServiceUtils.changeOrder(zhongHeConfig.getDeviceId(), 2));
        // 8 9 sn 帧数
        sb.append(ConvertCode.int2HexLittle((int) sn));
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
    }
}
