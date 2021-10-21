package vip.tuoyang.zhonghe.schedule;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import vip.tuoyang.zhonghe.bean.ZhongHeDto;
import vip.tuoyang.zhonghe.bean.ZhongHeResult;
import vip.tuoyang.zhonghe.bean.request.StateRequest;
import vip.tuoyang.zhonghe.bean.response.StateResponse;
import vip.tuoyang.zhonghe.config.properties.ServiceSystemProperties;
import vip.tuoyang.zhonghe.constants.StateEnum;
import vip.tuoyang.zhonghe.nettyclient.BroadcastClient;
import vip.tuoyang.zhonghe.service.ZhongHeClient;

/**
 * @author AlanSun
 * @date 2021/10/21 10:20
 */
@Slf4j
@Component
public class StateCheckSchedule {
    @Lazy
    @Autowired
    private ZhongHeClient zhongHeClient;
    @Lazy
    @Autowired
    private BroadcastClient broadcastClient;
    @Autowired
    private ServiceSystemProperties serviceSystemProperties;

    private volatile Integer lastState;

    /**
     * 每分钟检查状态
     */
    @Scheduled(cron = "0 0/1 * * * ?")
    public void stateCheck() {
        final ZhongHeResult<StateResponse> zhongHeResultState = zhongHeClient.state();
        if (zhongHeResultState.isSuccess()) {
            final int state = zhongHeResultState.getData().getState().equals(StateEnum.ONLINE_RUNNING) ? 1 : 0;
            if (lastState == null || !lastState.equals(state)) {
                log.info("状态变更; detail: [{}]", zhongHeResultState);
                ZhongHeDto<StateRequest> zhongHeBaseRequest1 = new ZhongHeDto<>();
                StateRequest stateRequest1 = new StateRequest();
                stateRequest1.setLabel(serviceSystemProperties.getZhongHeConfig().getLabel());
                stateRequest1.setState(state);
                zhongHeBaseRequest1.setCommand((byte) 13);
                zhongHeBaseRequest1.setData(stateRequest1);
                broadcastClient.sendMessage(zhongHeBaseRequest1);
                lastState = state;
            }
        } else {
            log.error("stateCheck error. detail: [{}]", zhongHeResultState);
        }
    }
}
