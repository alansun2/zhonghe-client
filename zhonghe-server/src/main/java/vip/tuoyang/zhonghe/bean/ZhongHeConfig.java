package vip.tuoyang.zhonghe.bean;

import lombok.Getter;
import lombok.Setter;
import vip.tuoyang.base.util.AssertUtils;
import vip.tuoyang.base.util.CheckUtils;

/**
 * @author AlanSun
 * @date 2021/8/22 12:52
 */
@Getter
@Setter
public class ZhongHeConfig {
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
