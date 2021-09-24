package vip.tuoyang.zhonghe.nettyserver;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import vip.tuoyang.base.constants.SeparatorConstants;
import vip.tuoyang.base.exception.BizException;

import java.util.concurrent.TimeUnit;

/**
 * @author AlanSun
 * @date 2021/9/23 9:39
 */
@ChannelHandler.Sharable
@Slf4j
public class InterceptorHandler extends SimpleChannelInboundHandler<String> {
    private final String secret;
    private static final String TOKEN_PREFIX = "token";
    private static final String HEARTBEAT_CONTENT = "Heartbeat";

    /**
     * 当链接建立后30秒内必须发送初始化数据，否则视该连接未非法连接，并关闭
     */
    final Cache<ChannelHandlerContext, ChannelHandlerContext> connectValidCheck = Caffeine.newBuilder()
            .expireAfterWrite(30, TimeUnit.SECONDS)
            .<ChannelHandlerContext, ChannelHandlerContext>removalListener((key, value, cause) -> {
                if (cause.wasEvicted()) {
                    log.warn("30 内未发送 token 数据，断开连接");
                    if (value != null) {
                        value.close();
                    }
                }
            }).build();

    public InterceptorHandler(String secret) {
        this.secret = secret;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        connectValidCheck.put(ctx, ctx);
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        connectValidCheck.invalidate(ctx);
        super.channelInactive(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) {
        if (msg.startsWith(TOKEN_PREFIX)) {
            if (msg.split(SeparatorConstants.COLON)[1].equals(secret)) {
                connectValidCheck.invalidate(ctx);
            } else {
                throw new BizException("token 校验不通过,msg: " + msg);
            }
        } else if (!HEARTBEAT_CONTENT.equals(msg)) {
            ctx.fireChannelRead(msg);
        }
    }
}
