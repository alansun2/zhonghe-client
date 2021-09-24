package vip.tuoyang.zhonghe.support;

import vip.tuoyang.zhonghe.bean.ZhongHeResult;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author AlanSun
 * @date 2021/9/22 15:06
 */
public class SyncSupport {

    public static Map<String, CountDownLatch2> labelCountDown2Map = new ConcurrentHashMap<>(64);

    public static Map<String, ZhongHeResult<?>> labelResultMap = new ConcurrentHashMap<>(64);

    public static CountDownLatch2 getCountDownLatch2(String label) {
        final CountDownLatch2 countDownLatch2 = labelCountDown2Map.get(label);
        if (countDownLatch2 == null) {
            final CountDownLatch2 countDownLatch21 = new CountDownLatch2(1);
            labelCountDown2Map.put(label, countDownLatch21);
            return countDownLatch21;
        } else {
            return countDownLatch2;
        }
    }
}
