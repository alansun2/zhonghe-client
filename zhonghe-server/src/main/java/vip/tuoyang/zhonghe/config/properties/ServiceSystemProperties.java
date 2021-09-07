package vip.tuoyang.zhonghe.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import vip.tuoyang.zhonghe.bean.ZhongHeConfig;

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
     * 是否开启 ip 上报
     */
    private boolean ipReportSwitch = true;
    /**
     * ip 改变时上报的地址
     */
    private String serverUrl;
    /**
     * 路径
     */
    private Path path;
    /**
     * 配置
     */
    private ZhongHeConfig zhongHeConfig;

    @Getter
    @Setter
    public static class Path {
        /**
         * 服务初始化
         */
        private String serverInit = "/common/init";
        /**
         * ip 改变
         */
        private String ipChange = "/common/ip-report";
    }
}
