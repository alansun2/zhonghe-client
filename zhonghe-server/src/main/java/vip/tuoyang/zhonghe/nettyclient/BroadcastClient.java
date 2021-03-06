package vip.tuoyang.zhonghe.nettyclient;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.HashedWheelTimer;
import lombok.extern.slf4j.Slf4j;
import vip.tuoyang.base.util.ThreadUtils;
import vip.tuoyang.zhonghe.bean.ZhongHeDto;
import vip.tuoyang.zhonghe.nettyclient.codec.MyStringEncoder;
import vip.tuoyang.zhonghe.nettyclient.codec.ZhongHeDtoEncoder;
import vip.tuoyang.zhonghe.service.ServiceHandler;
import vip.tuoyang.zhonghe.support.CountDownLatch2;
import vip.tuoyang.zhonghe.support.MyServiceCallback;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author AlanSun
 * @date 2021/9/22 17:05
 */
@Slf4j
public class BroadcastClient {
    protected final HashedWheelTimer timer = new HashedWheelTimer();

    private final ConnectorIdleStateTrigger idleStateTrigger = new ConnectorIdleStateTrigger();

    private final ServiceHandler serviceHandler;

    private final ZhongHeDtoEncoder zhongHeDtoEncoder;

    private final MyServiceCallback myServiceCallback;

    private Bootstrap boot;

    private Channel channel;

    private final int port;

    private final String host;

    private EventLoopGroup group;

    private final ExecutorService executorService;

    private final CountDownLatch2 countDownLatch2 = new CountDownLatch2(1);

    public BroadcastClient(ServiceHandler serviceHandler, ZhongHeDtoEncoder zhongHeDtoEncoder, MyServiceCallback myServiceCallback, int port, String host) {
        this.serviceHandler = serviceHandler;
        this.zhongHeDtoEncoder = zhongHeDtoEncoder;
        this.myServiceCallback = myServiceCallback;
        this.port = port;
        this.host = host;

        executorService = new ThreadPoolExecutor(4, 4, 0, TimeUnit.SECONDS, new ArrayBlockingQueue<>(50), r -> {
            Thread thread = new Thread(r);
            thread.setName("BroadcastClient");
            thread.setDaemon(true);
            return thread;
        });
    }

    /**
     * ??????
     */
    public void connect() {
        executorService.execute(() -> {
            group = new NioEventLoopGroup();

            boot = new Bootstrap();
            boot.group(group).channel(NioSocketChannel.class).handler(new LoggingHandler(LogLevel.INFO));

            final ConnectionWatchdog watchdog = new ConnectionWatchdog(this, boot, myServiceCallback, timer, port, host, true) {
                @Override
                public ChannelHandler[] handlers() {
                    return new ChannelHandler[]{
                            this,
                            new IdleStateHandler(0, 4, 0, TimeUnit.SECONDS),
//                            new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4),
                            new DelimiterBasedFrameDecoder(Integer.MAX_VALUE, Unpooled.copiedBuffer("%_@".getBytes())),
                            new StringDecoder(),
                            new MyStringEncoder(),
                            zhongHeDtoEncoder,
                            idleStateTrigger,
                            serviceHandler
                    };
                }
            };

            ChannelFuture future;
            //????????????
            try {
                synchronized (boot) {
                    boot.handler(new ChannelInitializer<Channel>() {
                        //?????????channel
                        @Override
                        protected void initChannel(Channel ch) {
                            ch.pipeline().addLast(watchdog.handlers());
                        }
                    });

                    future = boot.connect(host, port);
                }

                future.addListener(future1 -> {
                    if (future1.isSuccess()) {
                        channel = future.channel();
                        countDownLatch2.countDown();
                    }
                });

                // ???????????????synchronized???????????????????????????
                future.sync();
            } catch (Throwable t) {
                group.shutdownGracefully();
                ThreadUtils.shutdownAndAwaitTermination(executorService);
                log.error("????????????????????????connects to  fails", t);
                throw new RuntimeException();
            }
        });
    }

    public synchronized <T> void sendMessage(ZhongHeDto<T> zhongHeDto) {
        try {
            countDownLatch2.await();
        } catch (InterruptedException e) {
            log.error("????????????", e);
        }
        channel.writeAndFlush(zhongHeDto);
    }

    public synchronized void sendMessage(String msg) {
        try {
            countDownLatch2.await();
        } catch (InterruptedException e) {
            log.error("????????????", e);
        }
        channel.writeAndFlush(msg);
    }

    void setChannel(Channel channel) {
        this.channel = channel;
    }
}
