package vip.tuoyang.zhonghe.init;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import vip.tuoyang.zhonghe.service.ZhongHeClient;

/**
 * @author AlanSun
 * @date 2021/9/24 10:15
 */
@Lazy
@Component
public class ConnectZhongHeMiddleWare implements ApplicationRunner {
    @Autowired
    private ZhongHeClient zhongHeClient;
    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    /**
     * Callback used to run the bean.
     *
     * @param args incoming application arguments
     */
    @Override
    public void run(ApplicationArguments args) {
        taskExecutor.execute(() -> zhongHeClient.state());
    }
}
