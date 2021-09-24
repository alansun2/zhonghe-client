package vip.tuoyang.zhonghe.service.handler;

import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import lombok.extern.slf4j.Slf4j;
import vip.tuoyang.zhonghe.bean.ResultInternal;
import vip.tuoyang.zhonghe.bean.response.ZhongHeResponse;
import vip.tuoyang.zhonghe.constants.CmdEnum;
import vip.tuoyang.zhonghe.constants.StateEnum;
import vip.tuoyang.zhonghe.service.SendClient;
import vip.tuoyang.zhonghe.service.resulthandle.ResultHandlerContext;
import vip.tuoyang.zhonghe.support.SendStateHandler;
import vip.tuoyang.zhonghe.support.SyncResultSupport;
import vip.tuoyang.zhonghe.support.ZhongHeCallback;
import vip.tuoyang.zhonghe.support.ZhongHeClientLockProxy;
import vip.tuoyang.zhonghe.utils.ConvertCode;

/**
 * @author AlanSun
 * @date 2021/8/22 9:46
 **/
@Slf4j
public class MyClientHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    private final String label;

    private final ZhongHeCallback stateCallback;

    private final SendClient sendClient;

    public static final ThreadLocal<String> LABEL_THREAD_LOCAL = new InheritableThreadLocal<>();

    public MyClientHandler(String label, ZhongHeCallback stateCallback, SendClient sendClient) {
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
        LABEL_THREAD_LOCAL.set(label);

        try {
            if (zhongHeResponse.getCmdEnum() != CmdEnum.SEND_STATE) {
                final ResultInternal resultInternal = resultHandlerContext.handle(zhongHeResponse);
                resultInternal.setZhongHeResponse(zhongHeResponse);
                SyncResultSupport.labelResultInternal.put(label, resultInternal);
            } else {
                final SendStateHandler sendStateHandler = new SendStateHandler(zhongHeResponse);
                if (!sendStateHandler.isOnline().equals(StateEnum.TO_ONLINE_RUNNING)) {
                    sendStateHandler.handle(zhongHeResponse, label);
                    stateCallback.callback(label, sendStateHandler);
                }
                return;
            }

            // 需要等待获取数据的情况
            final ResultInternal resultInternal = SyncResultSupport.labelResultInternal.get(label);
            if (resultInternal != null) {
                final ZhongHeResponse zhongHeResponse1 = resultInternal.getZhongHeResponse();
                final CmdEnum cmdEnum = zhongHeResponse1.getCmdEnum();
                final String para = zhongHeResponse1.getPara();
                if ((cmdEnum != CmdEnum.PRO_TIMING_TASK || "01".equals(para))
                        && cmdEnum != CmdEnum.UPLOAD_MEDIA_FILE
                        && cmdEnum != CmdEnum.SEND_STATE) {
                    SyncResultSupport.getLabelResultCountDown(label).countDown();
                }
            }
        } finally {
            ZhongHeClientLockProxy.ZHONG_HE_CLIENT_THREAD_LOCAL.remove();
            LABEL_THREAD_LOCAL.remove();
        }
    }
}