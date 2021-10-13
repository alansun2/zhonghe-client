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
    private String secret = "dPO32$#kgJ5i&kjw1bjgdk34kbma13iYIo3*^";
    /**
     * 超时时间
     */
    private int timeout = 5;
    /**
     * tcp 端口号
     */
    private int tcpPort = 9000;
    /**
     * 读取超期时间，当客户端超过这个时间未发送给心跳后连接会断开。单位秒，默认30秒
     */
    private int readTimeOut = 90;
}
