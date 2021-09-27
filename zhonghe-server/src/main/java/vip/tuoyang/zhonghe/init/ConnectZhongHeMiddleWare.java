package vip.tuoyang.zhonghe.init;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import vip.tuoyang.zhonghe.config.properties.ServiceSystemProperties;
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

    /**
     * Callback used to run the bean.
     *
     * @param args incoming application arguments
     */
    @Override
    public void run(ApplicationArguments args) throws IOException {
        taskExecutor.execute(() -> zhongHeClient.state());
        if (serviceSystemProperties.isEnableWinTask()) {
            // 生成定时任务
            Runtime.getRuntime().exec("schtasks /Create /SC DAILY /TN 广播定时检查 /RI 1 /TR " + serviceSystemProperties.getBroadcastInstallPath().getInstallDir() + "/schedule-check.bat");
            Runtime.getRuntime().exec("schtasks /Create /SC DAILY /TN 广播定时重启 /ST 02:05 /TR " + serviceSystemProperties.getBroadcastInstallPath().getInstallDir() + "/restart.bat");
        }
    }
}
