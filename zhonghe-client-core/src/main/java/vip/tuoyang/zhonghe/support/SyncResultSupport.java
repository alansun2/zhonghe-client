package vip.tuoyang.zhonghe.support;

import vip.tuoyang.zhonghe.bean.ResultInternal;
import vip.tuoyang.zhonghe.bean.ZhongHeDownloadResult;
import vip.tuoyang.zhonghe.constants.CmdEnum;

import java.util.HashMap;
import java.util.Map;

/**
 * @author AlanSun
 * @date 2021/8/28 13:44
 */
public class SyncResultSupport {
    public static CountDownLatch2 downloadResultDataCountDown = new CountDownLatch2(1);

    public static ResultInternal resultInternal;

    public static Map<CmdEnum, ResultInternal> cmdResultMap = new HashMap<>(16);

    public static Map<String, ZhongHeDownloadResult> downloadParaResultMap = new HashMap<>(16);

    public static Map<CmdEnum, CountDownLatch2> cmdResultCountDownMap = new HashMap<>(16);

    static {
        for (CmdEnum value : CmdEnum.values()) {
            cmdResultCountDownMap.put(value, new CountDownLatch2(1));
        }
    }
}
