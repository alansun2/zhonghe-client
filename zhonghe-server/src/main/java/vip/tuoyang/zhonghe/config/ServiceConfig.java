package vip.tuoyang.zhonghe.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.config.Task;
import vip.tuoyang.zhonghe.config.properties.ServiceSystemProperties;
import vip.tuoyang.zhonghe.schedule.IpSchedule;

import javax.annotation.PostConstruct;

/**
 * @author AlanSun
 * @date 2021/9/4 16:29
 */
@Configuration(proxyBeanMethods = false)
public class ServiceConfig implements SchedulingConfigurer {
    @Autowired
    private IpSchedule ipSchedule;
    @Autowired
    private ServiceSystemProperties serviceSystemProperties;

    /**
     * Callback allowing a {@link TaskScheduler
     * TaskScheduler} and specific {@link Task Task}
     * instances to be registered against the given the {@link ScheduledTaskRegistrar}.
     *
     * @param taskRegistrar the registrar to be configured.
     */
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        if (serviceSystemProperties.isIpReportSwitch()) {
            taskRegistrar.addFixedDelayTask(ipSchedule::ipChangeListen, 10000);
        }
    }
}
