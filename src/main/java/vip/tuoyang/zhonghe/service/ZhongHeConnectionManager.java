package vip.tuoyang.zhonghe.service;

import vip.tuoyang.zhonghe.config.ZhongHeConfig;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author AlanSun
 * @date 2021/8/30 10:08
 */
public class ZhongHeConnectionManager {
    private final Map<String, ZhongHeClient> labelClientMap = new ConcurrentHashMap<>();

    /**
     * 初始化连接
     */
    public void initConnection(Map<String, ZhongHeConfig> labelConfigMap) {
        labelConfigMap.forEach((label, zhongHeConfig) -> {
            labelClientMap.put(label, ZhongHeClientImpl.create(zhongHeConfig));
        });
    }

    /**
     * 新增或重置连接
     */
    public void addOrResetConnection(String label, ZhongHeConfig zhongHeConfig) {
        final ZhongHeClient zhongHeClient = labelClientMap.get(label);
        if (zhongHeClient != null) {
            zhongHeClient.close();
        }
        labelClientMap.put(label, ZhongHeClientImpl.create(zhongHeConfig));
    }

    /**
     * 关闭连接
     */
    public void close() {
        labelClientMap.forEach((s, zhongHeClient) -> zhongHeClient.close());
    }
}
