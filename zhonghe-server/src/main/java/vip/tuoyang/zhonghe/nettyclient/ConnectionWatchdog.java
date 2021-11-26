package vip.tuoyang.zhonghe.nettyclient;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.util.Timeout;
import io.netty.util.Timer;
import io.netty.util.TimerTask;
import lombok.extern.slf4j.Slf4j;
import vip.tuoyang.zhonghe.support.MyServiceCallback;

import java.util.concurrent.TimeUnit;

/**
 * @author AlanSun
 * @date 2021/9/23 8:49
 * 重连检测狗，当发现当前的链路不稳定关闭之后，进行12次重连
 **/
@Slf4j
@Sharable
public abstract class ConnectionWatchdog extends ChannelInboundHandlerAdapter implements TimerTask, ChannelHandlerHolder {

    private final Bootstrap bootstrap;

    private final BroadcastClient broadcastClient;

    private final MyServiceCallback myServiceCallback;

    private final Timer timer;

    private final int port;

    private final String host;

    private final boolean reconnect;
    /**
     * 重试次数
     */
    private int curAttempts;

    private static final int MAX_ATTEMPTS = 16;

    public ConnectionWatchdog(BroadcastClient broadcastClient, Bootstrap bootstrap, MyServiceCallback myServiceCallback, Timer timer, int port, String host, boolean reconnect) {
        this.broadcastClient = broadcastClient;
        this.bootstrap = bootstrap;
        this.myServiceCallback = myServiceCallback;
        this.timer = timer;
        this.port = port;
        this.host = host;
        this.reconnect = reconnect;
    }

    /**
     * channel链路每次active的时候，将其连接的次数重新☞ 0
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        log.info("当前链路已经激活了，重连尝试次数重新置为0");
        curAttempts = 0;
        ctx.fireChannelActive();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        if (reconnect) {
            log.info("链接关闭，将进行重连");
            curAttempts++;
            // 重连的间隔时间会越来越长
            timer.newTimeout(this, this.getTimeOut(), TimeUnit.MILLISECONDS);
        } else {
            log.info("链接关闭, 不重连");
        }
        ctx.fireChannelInactive();
    }

    /**
     * 获取重试时间间隔
     *
     * @return 重试间隔时间， 大于最大次数时，每一分钟重试一次
     */
    private int getTimeOut() {
        if (curAttempts <= MAX_ATTEMPTS) {
            return curAttempts;
        } else {
            return 60000;
        }
    }

    @Override
    public void run(Timeout timeout) {
        ChannelFuture future;
        // bootstrap已经初始化好了，只需要将handler填入就可以了
        synchronized (bootstrap) {
            bootstrap.handler(new ChannelInitializer<Channel>() {
                @Override
                protected void initChannel(Channel ch) {
                    ch.pipeline().addLast(handlers());
                }
            });
            future = bootstrap.connect(host, port);
        }
        // future对象
        future.addListener((ChannelFutureListener) f -> {
            boolean succeed = f.isSuccess();

            // 如果重连失败，则调用ChannelInactive方法，再次出发重连事件，一直尝试12次，如果失败则不再重连
            if (!succeed) {
                log.info("重连失败, attempts: [{}]", curAttempts);
                f.channel().pipeline().fireChannelInactive();
            } else {
                broadcastClient.setChannel(f.channel());
                myServiceCallback.reconnect(broadcastClient);
                log.info("重连成功, attempts: [{}]", curAttempts);
            }
        });
    }
}