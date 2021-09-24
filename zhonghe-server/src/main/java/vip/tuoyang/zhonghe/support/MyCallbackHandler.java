package vip.tuoyang.zhonghe.support;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import vip.tuoyang.zhonghe.bean.ZhongHeDto;
import vip.tuoyang.zhonghe.bean.request.StateRequest;
import vip.tuoyang.zhonghe.constants.StateEnum;
import vip.tuoyang.zhonghe.nettyclient.BroadcastClient;

/**
 * @author AlanSun
 * @date 2021/9/22 23:28
 */
@Slf4j
public class MyCallbackHandler implements ZhongHeCallback {
    @Autowired
    private BroadcastClient broadcastClient;

    /**
     * 广播服务状态修改时回调
     *
     * @param label        label
     * @param stateHandler 状态处理器
     */
    @Override
    public void callback(String label, SendStateHandler stateHandler) {
        ZhongHeDto<StateRequest> zhongHeBaseRequest = new ZhongHeDto<>();
        StateRequest stateRequest = new StateRequest();
        stateRequest.setLabel(label);
        stateRequest.setState(stateHandler.isOnline().equals(StateEnum.ONLINE_RUNNING) ? 1 : 0);
        zhongHeBaseRequest.setCommand((byte) 13);
        zhongHeBaseRequest.setData(stateRequest);
        broadcastClient.sendMessage(zhongHeBaseRequest);
    }
}
