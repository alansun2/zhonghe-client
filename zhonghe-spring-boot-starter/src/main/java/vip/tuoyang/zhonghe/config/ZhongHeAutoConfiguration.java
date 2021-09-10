package vip.tuoyang.zhonghe.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import vip.tuoyang.base.util.SpringHelper;
import vip.tuoyang.zhonghe.config.properties.ServiceSystemProperties;
import vip.tuoyang.zhonghe.service.ZhongHeConnectionManager;
import vip.tuoyang.zhonghe.support.ZhongHeCallback;

import javax.annotation.PostConstruct;

/**
 * @author AlanSun
 * @date 2021/9/3 14:22
 */
@Import(SpringHelper.class)
@ComponentScan(basePackages = "vip.tuoyang.zhonghe")
@Configuration
public class ZhongHeAutoConfiguration {
    @Autowired
    private ServiceSystemProperties serviceSystemProperties;

    @PostConstruct
    public void init() {
        ZhongHeSystemProperties.secret = serviceSystemProperties.getSecret();
        ZhongHeSystemProperties.timeout = serviceSystemProperties.getTimeout();
    }

    @Autowired(required = false)
    private ZhongHeCallback zhongHeCallback;

    @ConditionalOnBean(ZhongHeCallback.class)
    @Bean
    public ZhongHeConnectionManager zhongHeConnectionManager() {
        return new ZhongHeConnectionManager(zhongHeCallback);
    }
}
