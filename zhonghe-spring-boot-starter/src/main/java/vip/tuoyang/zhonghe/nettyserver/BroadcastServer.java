package vip.tuoyang.zhonghe.nettyserver;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vip.tuoyang.zhonghe.config.properties.ServiceSystemProperties;
import vip.tuoyang.zhonghe.nettyserver.codec.MyStringEncoder;
import vip.tuoyang.zhonghe.nettyserver.codec.ZhongHeDtoEncoder;
import vip.tuoyang.zhonghe.service.ServiceHandler;
import vip.tuoyang.zhonghe.support.ServiceZhongHeCallback;

import javax.annotation.PostConstruct;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author AlanSun
 * @date 2021/9/22 13:46
 */
@Slf4j
@Service
public class BroadcastServer {
    @Autowired
    private ServiceSystemProperties serviceSystemProperties;
    @Autowired
    private ServiceZhongHeCallback serviceZhongHeCallback;

    private final ExecutorService executorService = new ThreadPoolExecutor(4, 4, 0, TimeUnit.SECONDS, new ArrayBlockingQueue<>(50), r -> {
        Thread thread = new Thread(r);
        thread.setName("BroadcastServer");
        thread.setDaemon(true);
        return thread;
    });

    @PostConstruct
    public void init() {
        executorService.execute(() -> {
            EventLoopGroup bossGroup = new NioEventLoopGroup();
            EventLoopGroup workerGroup = new NioEventLoopGroup();
            try {
                ServerBootstrap sbs = new ServerBootstrap();
                // 配置nio服务参数
                sbs.group(bossGroup, workerGroup)
                        .channel(NioServerSocketChannel.class)
                        // tcp最大缓存链接个数
                        .option(ChannelOption.SO_BACKLOG, 256)
                        //保持连接
                        .childOption(ChannelOption.SO_KEEPALIVE, true)
                        // 打印日志级别
                        .handler(new LoggingHandler(LogLevel.INFO))
                        .childHandler(new ChannelInitializer<SocketChannel>() {

                            @Override
                            protected void initChannel(SocketChannel socketChannel) {
                                socketChannel.pipeline().addLast(new ReadTimeoutHandler(serviceSystemProperties.getReadTimeOut()))
//                                        .addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4))
                                        .addLast(new DelimiterBasedFrameDecoder(Integer.MAX_VALUE, Unpooled.copiedBuffer("%_@".getBytes())))
                                        .addLast(new StringDecoder())
                                        .addLast(new MyStringEncoder())
                                        .addLast(new ZhongHeDtoEncoder())
                                        .addLast(new InterceptorHandler(serviceSystemProperties.getSecret()))
                                        .addLast(new ServiceHandler(serviceZhongHeCallback));
                            }
                        });
                // 绑定端口，开始接受链接
                ChannelFuture cf = sbs.bind(serviceSystemProperties.getTcpPort()).sync();

                cf.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                log.error("中断异常", e);
            } finally {
                bossGroup.shutdownGracefully();
                workerGroup.shutdownGracefully();
            }
        });
    }
}
