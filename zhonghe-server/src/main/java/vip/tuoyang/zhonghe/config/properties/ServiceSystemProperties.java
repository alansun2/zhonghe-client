package vip.tuoyang.zhonghe.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import vip.tuoyang.base.util.AssertUtils;
import vip.tuoyang.zhonghe.bean.BroadcastInstallPath;

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
    private String fileDir = "D:\\opt\\zhonghe\\file\\";
    /**
     * 请求密码
     */
    private String secret = "dPO32$#kgJ5i&kjw1bjgdk34kbma13iYIo3*^";
    /**
     * 超时时间
     */
    private Integer timeout = 5;

    private Integer tcpPort;

    private String tcpHost;

    /**
     * 是否启用 windows 任务, 默认启用
     */
    private boolean enableWinTask = true;

    /**
     * 配置
     */
    private vip.tuoyang.zhonghe.config.ZhongHeConfig zhongHeConfig = new vip.tuoyang.zhonghe.config.ZhongHeConfig();

    /**
     * 广播 nas 和中间件的安装目录, 测试时使用
     */
    private String installDir = "D:\\project\\jszn\\broadcast\\zhonghe-broadcast";

    private BroadcastInstallPath broadcastInstallPath;

    public void valid() {
        AssertUtils.notNull(tcpPort, "tcpPort 必填");
        AssertUtils.notNull(tcpHost, "tcpHost 必填");
    }

    /**
     * @author AlanSun
     * @date 2021/8/22 12:52
     */
    @Getter
    @Setter
    @Configuration(proxyBeanMethods = false)
    @ConfigurationProperties(value = "system.zhong-he-config")
    public static class ZhongHeConfig {
        /**
         * 设备 id
         */
        private String deviceId;
        /**
         * 管理码
         */
        private String managerCode;
        /**
         * 本地绑定端口
         */
        private Integer localBindPort;
        /**
         * 设备描述
         */
        private String deviceDes;
        /**
         * 中间件 ip
         */
        private String middleWareIp;
        /**
         * 中间件端口号
         */
        private Integer middleWarePort = 8607;
        /**
         * 中间件采播端口号
         */
        private Integer middleWareCapturePort = 8608;
        /**
         * nas 的 ip
         */
        private String nasIp;
        /**
         * nas 的连接端口
         * 2048～48000
         */
        private Integer nasConnectPort = 8100;
        /**
         * nas 的控制端口
         * 2048～48000
         */
        private Integer nasControlPort = 8101;
        /**
         * nas 的采播端口
         * 2048～48000
         */
        private Integer nasCapturePort = 8201;

        private String label;

        private String deviceAddress;
    }
}
