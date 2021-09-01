package vip.tuoyang.zhonghe.service.handler;

import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import lombok.extern.slf4j.Slf4j;
import vip.tuoyang.zhonghe.bean.ResultInternal;
import vip.tuoyang.zhonghe.bean.response.ZhongHeResponse;
import vip.tuoyang.zhonghe.constants.CmdEnum;
import vip.tuoyang.zhonghe.service.SendClient;
import vip.tuoyang.zhonghe.service.resulthandle.ResultHandlerContext;
import vip.tuoyang.zhonghe.support.SendStateHandler;
import vip.tuoyang.zhonghe.support.StateCallback;
import vip.tuoyang.zhonghe.support.SyncResultSupport;
import vip.tuoyang.zhonghe.support.ZhongHeClientLockProxy;
import vip.tuoyang.zhonghe.utils.ConvertCode;

/**
 * @author AlanSun
 * @date 2021/8/22 9:46
 **/
@Slf4j
public class MyClientHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    private final String label;

    private final StateCallback stateCallback;

    private final SendClient sendClient;

    public MyClientHandler(String label, StateCallback stateCallback, SendClient sendClient) {
        this.label = label;
        this.stateCallback = stateCallback;
        this.sendClient = sendClient;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket packet) {
        final String receiverData = ConvertCode.bytes2HexString(ByteBufUtil.getBytes(packet.content()));
        final ZhongHeResponse zhongHeResponse = ZhongHeResponse.parse(receiverData);
        log.info("接收到响应帧: [{}]", zhongHeResponse);
        final ResultHandlerContext resultHandlerContext = ResultHandlerContext.create(zhongHeResponse.getCmdEnum());
        ZhongHeClientLockProxy.ZHONG_HE_CLIENT_THREAD_LOCAL.set(sendClient);
        ZhongHeClientLockProxy.LABEL_THREAD_LOCAL.set(label);
        try {
            if (zhongHeResponse.getCmdEnum() != CmdEnum.SEND_STATE) {
                final ResultInternal resultInternal = resultHandlerContext.handle(zhongHeResponse);
                resultInternal.setZhongHeResponse(zhongHeResponse);
                SyncResultSupport.labelResultInternal.put(label, resultInternal);
            } else {
                SendStateHandler.handle(zhongHeResponse, label);
                stateCallback.callback(label, new SendStateHandler(zhongHeResponse));
            }

            if (zhongHeResponse.getCmdEnum() != CmdEnum.PRO_TIMING_TASK) {
                SyncResultSupport.labelResultCountDownMap.get(label).countDown();
            }
        } finally {
            ZhongHeClientLockProxy.ZHONG_HE_CLIENT_THREAD_LOCAL.remove();
            ZhongHeClientLockProxy.LABEL_THREAD_LOCAL.remove();
        }
    }
}