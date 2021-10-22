package vip.tuoyang.zhonghe.support;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import vip.tuoyang.zhonghe.bean.ZhongHeDto;
import vip.tuoyang.zhonghe.bean.ZhongHeResult;
import vip.tuoyang.zhonghe.bean.request.StateRequest;
import vip.tuoyang.zhonghe.bean.response.StateResponse;
import vip.tuoyang.zhonghe.constants.StateEnum;
import vip.tuoyang.zhonghe.nettyclient.BroadcastClient;
import vip.tuoyang.zhonghe.service.ZhongHeClient;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * @author AlanSun
 * @date 2021/9/22 23:28
 */
@Slf4j
public class MyCallbackHandler implements ZhongHeCallback {
    @Autowired
    private BroadcastClient broadcastClient;
    @Lazy
    @Autowired
    private ZhongHeClient zhongHeClient;

    private ScheduledFuture<?> scheduledFuture;

    private Long lastOfflineTime;

    private volatile Integer lastState;

    private final ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(1, r -> {
        Thread thread = new Thread(r);
        thread.setName("MyCallbackHandler");
        return thread;
    });

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

            if (state == 0 && scheduledFuture == null) {
                scheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
                    log.info("重连");
                    boolean flag;
                    try {
                        final ZhongHeResult<StateResponse> zhongHeResultSate = zhongHeClient.state();
                        flag = zhongHeResultSate.isSuccess() && zhongHeResultSate.getData().getState().equals(StateEnum.ONLINE_RUNNING);
                    } catch (Exception e) {
                        flag = false;
                    }

                    if (flag) {
                        ZhongHeDto<StateRequest> zhongHeBaseRequest1 = new ZhongHeDto<>();
                        StateRequest stateRequest1 = new StateRequest();
                        stateRequest1.setLabel(label);
                        stateRequest1.setState(1);
                        zhongHeBaseRequest1.setCommand((byte) 13);
                        zhongHeBaseRequest1.setData(stateRequest1);
                        broadcastClient.sendMessage(zhongHeBaseRequest1);
                        this.cancelTask();
                    }
                }, 0, 1, TimeUnit.MINUTES);
            }
        } else {
            log.info("状态未变化");
        }
    }

    private void cancelTask() {
        for (int i = 0; i < 60; i++) {
            if (scheduledFuture == null) {
                LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(1));
            } else {
                scheduledFuture.cancel(true);
                scheduledFuture = null;
                break;
            }
        }
    }
}
