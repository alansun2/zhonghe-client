package vip.tuoyang.zhonghe.support;

import vip.tuoyang.zhonghe.bean.ResultInternal;
import vip.tuoyang.zhonghe.bean.ZhongHeDownloadResult;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author AlanSun
 * @date 2021/8/28 13:44
 */
public class SyncResultSupport {
    public static Map<String, ResultInternal> labelResultInternal = new ConcurrentHashMap<>(16);

    public static Map<String, ZhongHeDownloadResult> labelDownloadResultMap = new ConcurrentHashMap<>(16);

    public static Map<String, CountDownLatch2> labelDownloadResultDataCountDown = new ConcurrentHashMap<>(16);

    public static Map<String, CountDownLatch2> labelResultCountDownMap = new ConcurrentHashMap<>(16);

    public static void initCountDown(Collection<String> labels) {
        labels.forEach(label -> {
            labelDownloadResultDataCountDown.put(label, new CountDownLatch2(1));
            labelResultCountDownMap.put(label, new CountDownLatch2(1));
        });
    }
}
