package vip.tuoyang.zhonghe.init;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import vip.tuoyang.zhonghe.bean.ZhongHeDto;
import vip.tuoyang.zhonghe.bean.ZhongHeResult;
import vip.tuoyang.zhonghe.bean.request.StateRequest;
import vip.tuoyang.zhonghe.bean.response.StateResponse;
import vip.tuoyang.zhonghe.config.properties.ServiceSystemProperties;
import vip.tuoyang.zhonghe.constants.StateEnum;
import vip.tuoyang.zhonghe.nettyclient.BroadcastClient;
import vip.tuoyang.zhonghe.service.CommonService;
import vip.tuoyang.zhonghe.service.ZhongHeClient;

import java.io.IOException;

/**
 * @author AlanSun
 * @date 2021/9/24 10:15
 */
@Slf4j
@Lazy
@Component
public class ConnectZhongHeMiddleWare implements ApplicationRunner {
    @Autowired
    private ZhongHeClient zhongHeClient;
    @Autowired
    private ServiceSystemProperties serviceSystemProperties;
    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;
    @Autowired
    private CommonService commonService;
    @Autowired
    private BroadcastClient broadcastClient;

    /**
     * Callback used to run the bean.
     *
     * @param args incoming application arguments
     */
    @Override
    public void run(ApplicationArguments args) throws IOException {
        taskExecutor.execute(() -> {
            final ZhongHeResult<StateResponse> state = zhongHeClient.state();
            ZhongHeDto<StateRequest> zhongHeBaseRequest1 = new ZhongHeDto<>();
            StateRequest stateRequest1 = new StateRequest();
            stateRequest1.setLabel(serviceSystemProperties.getZhongHeConfig().getLabel());
            stateRequest1.setState(1);
            zhongHeBaseRequest1.setCommand((byte) 13);
            zhongHeBaseRequest1.setData(stateRequest1);
            if (state.isSuccess() && state.getData().getState().equals(StateEnum.ONLINE_RUNNING)) {
                broadcastClient.sendMessage(zhongHeBaseRequest1);
            }
        });
        if (serviceSystemProperties.isEnableWinTask()) {
            // 生成定时任务
            commonService.generatorTimer();
        }
    }
}
