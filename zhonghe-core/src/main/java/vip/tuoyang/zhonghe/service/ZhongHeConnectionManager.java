package vip.tuoyang.zhonghe.service;

import vip.tuoyang.base.util.ThreadUtils;
import vip.tuoyang.zhonghe.config.ZhongHeConfig;
import vip.tuoyang.zhonghe.support.SyncResultSupport;
import vip.tuoyang.zhonghe.support.ZhongHeCallback;
import vip.tuoyang.zhonghe.support.ZhongHeClientLockProxy;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author AlanSun
 * @date 2021/8/30 10:08
 */
public class ZhongHeConnectionManager {
    private final Map<String, ZhongHeClient> labelClientMap = new ConcurrentHashMap<>();

    private final ZhongHeCallback stateCallback;

    private static final ThreadPoolExecutor THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(10, 50, 0, TimeUnit.SECONDS, new LinkedBlockingQueue<>(), r -> {
        final Thread thread = new Thread(r);
        thread.setName("zhonghe-connection-manager");
        return thread;
    });

    public ZhongHeConnectionManager(ZhongHeCallback stateCallback) {
        this.stateCallback = stateCallback;
    }

    /**
     * 初始化连接
     */
    public void initConnection(Map<String, ZhongHeConfig> labelConfigMap) {
        labelConfigMap.forEach((label, zhongHeConfig) -> {
            labelClientMap.put(label, ZhongHeClientLockProxy.getProxy(ZhongHeClientImpl.create(zhongHeConfig, label, stateCallback), label));
        });

        SyncResultSupport.initCountDown(labelConfigMap.keySet());
    }

    /**
     * 新增或重置连接
     */
    public void addOrResetConnection(ZhongHeConfig zhongHeConfig) {
        final String label = zhongHeConfig.getLabel();
        final ZhongHeClient zhongHeClient = labelClientMap.get(label);
        if (zhongHeClient != null) {
            zhongHeClient.close(true);
            labelClientMap.remove(label);
        }

        final ZhongHeClient proxy = ZhongHeClientLockProxy.getProxy(ZhongHeClientImpl.create(zhongHeConfig, label, stateCallback), label);
        labelClientMap.put(label, proxy);
        THREAD_POOL_EXECUTOR.execute(proxy::state);
    }

    /**
     * 关闭连接
     */
    public void close() {
        labelClientMap.forEach((s, zhongHeClient) -> zhongHeClient.getSendClient().close());
        ThreadUtils.shutdownAndAwaitTermination(THREAD_POOL_EXECUTOR);
    }

    /**
     * 获取client
     *
     * @param label label
     * @return {@link ZhongHeClient}
     */
    public ZhongHeClient getZhongHeClient(String label) {
        return labelClientMap.get(label);
    }
}
