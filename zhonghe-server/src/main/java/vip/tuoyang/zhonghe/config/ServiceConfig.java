package vip.tuoyang.zhonghe.config;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.config.Task;
import vip.tuoyang.base.exception.BizException;
import vip.tuoyang.base.util.HttpClientUtils;
import vip.tuoyang.base.util.IpUtils;
import vip.tuoyang.base.util.bean.HttpParams;
import vip.tuoyang.zhonghe.bean.ZhongHeConfig;
import vip.tuoyang.zhonghe.config.properties.ServiceSystemProperties;
import vip.tuoyang.zhonghe.schedule.IpSchedule;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * @author AlanSun
 * @date 2021/9/4 16:29
 */
@Slf4j
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
    public void configureTasks(@NonNull ScheduledTaskRegistrar taskRegistrar) {
        if (serviceSystemProperties.isIpReportSwitch()) {
            taskRegistrar.addFixedDelayTask(ipSchedule::ipChangeListen, 10000);
        }
    }

    @PostConstruct
    public void init() throws UnsupportedEncodingException {
        final ZhongHeConfig zhongHeConfig = serviceSystemProperties.getZhongHeConfig();
        zhongHeConfig.valid();

        final String publicIp = IpUtils.getPublicIp();
        zhongHeConfig.setNasIp(publicIp);
        zhongHeConfig.setMiddleWareIp(publicIp);

        BasicHeader[] basicHeaders = new BasicHeader[2];
        basicHeaders[0] = new BasicHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        basicHeaders[1] = new BasicHeader("secret", serviceSystemProperties.getSecret());
        final HttpParams httpParams = HttpParams.builder()
                .url(serviceSystemProperties.getServerUrl() + serviceSystemProperties.getPath().getServerInit())
                .headers(basicHeaders)
                .httpEntity(new StringEntity(JSON.toJSONString(zhongHeConfig))).build();
        final HttpResponse httpResponse;
        try {
            httpResponse = HttpClientUtils.doPost(httpParams);
        } catch (IOException e) {
            log.error("启动失败,未连接到设备服务器", e);
            throw new BizException("启动失败,未连接到设备服务器");
        }
        if (httpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
            throw new BizException("启动失败,未连接到设备服务器");
        }
    }
}
