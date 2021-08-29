package vip.tuoyang.zhonghe.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import vip.tuoyang.zhonghe.config.ZhongHeConfig;
import vip.tuoyang.zhonghe.constants.CmdEnum;
import vip.tuoyang.zhonghe.utils.ConvertCode;
import vip.tuoyang.zhonghe.utils.ServiceUtils;

/**
 * @author AlanSun
 * @date 2021/8/22 12:42
 **/
@Slf4j
public class ZhongHeControlClient {
    private final ZhongHeConfig zhongHeConfig;

    public ZhongHeControlClient(ZhongHeConfig zhongHeConfig) {
        zhongHeConfig.valid();
        this.zhongHeConfig = zhongHeConfig;
    }

    /**
     * test
     */
    public void test() {
        SendClient.getSingleton().send(CmdEnum.TEST, "00", null);
    }

    /**
     * 初始化中间件
     */
    public void initMiddleWare() {
        // 先关闭
        this.close();

        //// 构建初始化数据
        StringBuilder sb = new StringBuilder();
        // 16 17 deviceId
        sb.append(ServiceUtils.changeOrder(zhongHeConfig.getDeviceId(), 2));
        // 18 19 管理码
        sb.append(ServiceUtils.changeOrder(zhongHeConfig.getManagerCode(), 2));
        // 20 - 51描述
        sb.append(StringUtils.rightPad(ConvertCode.intToHexString(zhongHeConfig.getDeviceDes().length() * 2, 1) + ConvertCode.bytes2HexString(ServiceUtils.toGbkBytes(zhongHeConfig.getDeviceDes())).toUpperCase(), 64, '0'));
        // 52 53 54, 55 nas ip
        final String nasIp = zhongHeConfig.getNasIp();
        final String[] nasIpSplit = nasIp.split("\\.");
        for (String nasIpSegment : nasIpSplit) {
            sb.append(ConvertCode.intToHexString(Integer.parseInt(nasIpSegment), 1).toUpperCase());
        }
        // 56 57 连接端口
        sb.append(ConvertCode.int2HexLittle(zhongHeConfig.getNasConnectPort()));
        // 58 59 控制端口
        sb.append(ConvertCode.int2HexLittle(zhongHeConfig.getNasControlPort()));
        // 60 61 采播端口
        sb.append(ConvertCode.int2HexLittle(zhongHeConfig.getNasCapturePort()));
        // 62 63
        sb.append("0000");

        SendClient.getSingleton().send(CmdEnum.INIT, "00", sb.toString());
    }

    /**
     * 关闭
     */
    public void close() {
        SendClient.getSingleton().send(CmdEnum.CLOSE, "00", null);
    }
}