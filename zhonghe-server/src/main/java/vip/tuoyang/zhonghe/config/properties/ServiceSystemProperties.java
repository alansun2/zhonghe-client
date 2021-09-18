package vip.tuoyang.zhonghe.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import vip.tuoyang.base.util.AssertUtils;
import vip.tuoyang.base.util.CheckUtils;
import vip.tuoyang.zhonghe.bean.request.BroadcastInstallPath;

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
     * 是否开启 ip 上报
     */
    private boolean ipReportSwitch = true;
    /**
     * ip 改变时上报的地址
     */
    private String serverUrl;
    /**
     * 文件上传路径
     */
    private String fileUploadPath = "/common/upload-file";
    /**
     * 路径
     */
    private Path path = new Path();
    /**
     * 配置
     */
    private ZhongHeConfig zhongHeConfig = new ZhongHeConfig();

    /**
     * 广播 nas 和中间件的安装目录
     */
    private String installDir;

    private BroadcastInstallPath broadcastInstallPath;

    @Getter
    @Setter
    public static class Path {
        /**
         * 服务初始化
         */
        private String serverInit = "/common/server-init";
        /**
         * ip 改变
         */
        private String ipChange = "/common/ip-report";
    }

    /**
     * @author AlanSun
     * @date 2021/8/22 12:52
     */
    @Getter
    @Setter
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

        private String fileUploadUrl;

        public void valid() {
            AssertUtils.notBlank(deviceId, "deviceId 必填");
            AssertUtils.notBlank(managerCode, "managerCode 必填");
            AssertUtils.notBlank(deviceDes, "deviceDes 必填");
            AssertUtils.notNull(nasConnectPort, "nasConnectPort 必填");
            AssertUtils.notNull(nasControlPort, "nasControlPort 必填");
            AssertUtils.notNull(nasCapturePort, "nasCapturePort 必填");
            AssertUtils.notNull(label, "label 必填");
            AssertUtils.notNull(deviceAddress, "deviceAddress 必填");

            AssertUtils.isTrue(CheckUtils.portCheck(middleWarePort, nasConnectPort, nasControlPort, nasCapturePort), "请检查端口范围，0~65535");
        }
    }

    public void valid() {
        AssertUtils.notNull(serverUrl, "服务地址必填");
    }
}
