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

    private static final Map<String, CountDownLatch2> LABEL_DOWNLOAD_RESULT_DATA_COUNT_DOWN = new ConcurrentHashMap<>(16);

    private static final Map<String, CountDownLatch2> LABEL_RESULT_COUNT_DOWN_MAP = new ConcurrentHashMap<>(16);

    public static void initCountDown(Collection<String> labels) {
        labels.forEach(label -> {
            LABEL_DOWNLOAD_RESULT_DATA_COUNT_DOWN.put(label, new CountDownLatch2(1));
            LABEL_RESULT_COUNT_DOWN_MAP.put(label, new CountDownLatch2(1));
        });
    }

    public synchronized static CountDownLatch2 getLabelResultCountDown(String label) {
        final CountDownLatch2 countDownLatch2 = LABEL_RESULT_COUNT_DOWN_MAP.get(label);
        if (countDownLatch2 == null) {
            LABEL_RESULT_COUNT_DOWN_MAP.put(label, new CountDownLatch2(1));
        }

        return LABEL_RESULT_COUNT_DOWN_MAP.get(label);
    }

    public synchronized static CountDownLatch2 getLabelDownloadResultDataCount(String label) {
        final CountDownLatch2 countDownLatch2 = LABEL_DOWNLOAD_RESULT_DATA_COUNT_DOWN.get(label);
        if (countDownLatch2 == null) {
            LABEL_DOWNLOAD_RESULT_DATA_COUNT_DOWN.put(label, new CountDownLatch2(1));
        }

        return LABEL_DOWNLOAD_RESULT_DATA_COUNT_DOWN.get(label);
    }
}
