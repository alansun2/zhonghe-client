package vip.tuoyang.zhonghe.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import vip.tuoyang.zhonghe.config.properties.ServiceSystemProperties;

import javax.annotation.PostConstruct;

/**
 * @author AlanSun
 * @date 2021/9/3 14:22
 */
@ComponentScan(basePackages = "vip.tuoyang.zhonghe")
@Configuration
public class ZhongHeAutoConfiguration {
    @Autowired
    private ServiceSystemProperties serviceSystemProperties;

    @PostConstruct
    public void init() {
        ZhongHeSystemProperties.secret = serviceSystemProperties.getSecret();
    }
}
