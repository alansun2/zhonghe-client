package vip.tuoyang.zhonghe.service;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import lombok.extern.slf4j.Slf4j;
import vip.tuoyang.zhonghe.bean.ResultInternal;
import vip.tuoyang.zhonghe.config.ZhongHeConfig;
import vip.tuoyang.zhonghe.constants.CmdEnum;
import vip.tuoyang.zhonghe.service.handler.MyChannelInitializer;
import vip.tuoyang.zhonghe.support.StateCallback;
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

    private final StateCallback stateCallback;

    public SendClient(ZhongHeConfig zhongHeConfig, String label, StateCallback stateCallback) {
        this.zhongHeConfig = zhongHeConfig;
        inetSocketAddress = new InetSocketAddress(zhongHeConfig.getMiddleWareIp(), zhongHeConfig.getMiddleWarePort());
        this.startListener();
        this.label = label;
        this.stateCallback = stateCallback;
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
                        .handler(new MyChannelInitializer(label, stateCallback, this));
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
        if (channel == null) {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (channel == null) {
                try {
                    this.startListener();
                } catch (Exception e) {
                    log.warn("启动客户端失败");
                }
            }
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return channel;
    }

    public void close() {
        if (channel.isActive()) {
            channel.close();
        }
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
                SyncResultSupport.labelResultCountDownMap.get(label).await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ResultInternal resultInternal = SyncResultSupport.labelResultInternal.get(label);
            if (resultInternal == null) {
                log.info("cmd: [{}], para: [{}], 收到: [{}]", cmdEnum, para, "10秒超时");
                resultInternal = new ResultInternal();
                resultInternal.setSuccess(false);
                resultInternal.setErrorMsg("超时");
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
