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
     * 文件地址
     */
    private String fileDir;
    /**
     * 请求密码
     */
    private String secret;
    /**
     * ip 改变时上报的地址
     */
    private String ipReportUrl;
    /**
     * 标签
     */
    private String label;
    /**
     * 是否开启 ip 上报
     */
    private boolean ipReportSwitch = true;
}
