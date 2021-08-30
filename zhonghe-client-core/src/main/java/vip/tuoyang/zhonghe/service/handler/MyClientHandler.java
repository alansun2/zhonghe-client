package vip.tuoyang.zhonghe.service.handler;

import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import lombok.extern.slf4j.Slf4j;
import vip.tuoyang.zhonghe.bean.response.ZhongHeResponse;
import vip.tuoyang.zhonghe.constants.CmdEnum;
import vip.tuoyang.zhonghe.service.StateHandler;
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
        log.info("接收到响应帧: [{}]", zhongHeResponse);
        final ResultHandlerContext resultHandlerContext = ResultHandlerContext.create(zhongHeResponse.getCmdEnum());
        if (zhongHeResponse.getCmdEnum() != CmdEnum.SEND_STATE) {
            SyncResultSupport.resultInternal = resultHandlerContext.handle(zhongHeResponse);
            SyncResultSupport.resultInternal.setZhongHeResponse(zhongHeResponse);
        } else {
            StateHandler.fillData(zhongHeResponse);
        }

        if (zhongHeResponse.getCmdEnum() != CmdEnum.PRO_TIMING_TASK && zhongHeResponse.getCmdEnum() != CmdEnum.REQUEST_EDITABLE_TASK) {
            SyncResultSupport.cmdResultCountDownMap.get(zhongHeResponse.getCmdEnum()).countDown();
        }
    }
}