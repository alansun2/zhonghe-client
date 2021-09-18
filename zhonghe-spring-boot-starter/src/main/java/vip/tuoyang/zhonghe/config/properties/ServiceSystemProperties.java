package vip.tuoyang.zhonghe.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author AlanSun
 * @date 2021/9/3 12:46
 */
@Getter
@Setter
@Configuration(proxyBeanMethods = false)
@ConfigurationProperties(value = "system")
public class ServiceSystemProperties {
    /**
     * 请求密码
     */
    private String secret;
    /**
     * 超时时间
     */
    private int timeout = 5;
}
