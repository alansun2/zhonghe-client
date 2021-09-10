package vip.tuoyang.zhonghe.service;

import vip.tuoyang.zhonghe.config.ZhongHeConfig;
import vip.tuoyang.zhonghe.support.SyncResultSupport;
import vip.tuoyang.zhonghe.support.ZhongHeCallback;
import vip.tuoyang.zhonghe.support.ZhongHeClientLockProxy;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author AlanSun
 * @date 2021/8/30 10:08
 */
public class ZhongHeConnectionManager {
    private final Map<String, ZhongHeClient> labelClientMap = new ConcurrentHashMap<>();

    private final ZhongHeCallback stateCallback;

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
    public void addOrResetConnection(String label, ZhongHeConfig zhongHeConfig) {
        final ZhongHeClient zhongHeClient = labelClientMap.get(label);
        if (zhongHeClient != null) {
            zhongHeClient.close(true);
            labelClientMap.remove(label);
        }
        labelClientMap.put(label, ZhongHeClientLockProxy.getProxy(ZhongHeClientImpl.create(zhongHeConfig, label, stateCallback), label));
    }

    /**
     * 关闭连接
     */
    public void close() {
        labelClientMap.forEach((s, zhongHeClient) -> zhongHeClient.close(true));
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
