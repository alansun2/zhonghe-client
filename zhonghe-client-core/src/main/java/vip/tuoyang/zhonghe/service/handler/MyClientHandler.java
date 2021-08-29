package vip.tuoyang.zhonghe.service.handler;

import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import lombok.extern.slf4j.Slf4j;
import vip.tuoyang.zhonghe.bean.response.ZhongHeResponse;
import vip.tuoyang.zhonghe.service.resulthandle.ResultHandlerContext;
import vip.tuoyang.zhonghe.support.SyncResultSupport;
import vip.tuoyang.zhonghe.utils.ConvertCode;

/**
 * @author AlanSun
 * @date 2021/8/22 9:46
 **/
@Slf4j
public class MyClientHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket packet) {
        final String receiverData = ConvertCode.bytes2HexString(ByteBufUtil.getBytes(packet.content()));
        final ZhongHeResponse zhongHeResponse = ZhongHeResponse.parse(receiverData);
        final ResultHandlerContext resultHandlerContext = ResultHandlerContext.create(zhongHeResponse.getCmdEnum());
        SyncResultSupport.resultInternal = resultHandlerContext.handle(zhongHeResponse);
        SyncResultSupport.cmdResultCountDownMap.get(zhongHeResponse.getCmdEnum()).countDown();
    }
}