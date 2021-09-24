package vip.tuoyang.zhonghe.nettyserver;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import vip.tuoyang.base.constants.SeparatorConstants;
import vip.tuoyang.base.exception.BizException;

/**
 * @author AlanSun
 * @date 2021/9/23 9:39
 */
@ChannelHandler.Sharable
@Slf4j
public class InterceptorHandler extends SimpleChannelInboundHandler<String> {

    private final String secret;

    public InterceptorHandler(String secret) {
        this.secret = secret;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) {
        if (msg.startsWith("token")) {
            if (msg.split(SeparatorConstants.COLON)[1].equals(secret)) {
            } else {
                throw new BizException("token 校验不通过,msg: " + msg);
            }
        } else if (!"Heartbeat".equals(msg)) {
            ctx.fireChannelRead(msg);
        }
    }
}
