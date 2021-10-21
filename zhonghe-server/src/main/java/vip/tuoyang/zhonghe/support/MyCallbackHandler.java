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

    private Long lastOfflineTime;

    private volatile Integer lastState;

    /**
     * 广播服务状态修改时回调
     *
     * @param label        label
     * @param stateHandler 状态处理器
     */
    @Override
    public synchronized void callback(String label, SendStateHandler stateHandler) {
        final int state = stateHandler.isOnline().equals(StateEnum.ONLINE_RUNNING) ? 1 : 0;
        log.info("服务状态变更，state: [{}]", state);
        if (lastState == null || !lastState.equals(state)) {
            if (state == 0 && lastOfflineTime != null && lastOfflineTime + 5000 > System.currentTimeMillis()) {
                log.info("0 抑制");
                return;
            }
            ZhongHeDto<StateRequest> zhongHeBaseRequest = new ZhongHeDto<>();
            StateRequest stateRequest = new StateRequest();
            stateRequest.setLabel(label);
            stateRequest.setState(state);
            zhongHeBaseRequest.setCommand((byte) 13);
            zhongHeBaseRequest.setData(stateRequest);
            broadcastClient.sendMessage(zhongHeBaseRequest);
            lastOfflineTime = System.currentTimeMillis();
            lastState = state;
        } else {
            log.info("状态未变化");
        }
    }
}
