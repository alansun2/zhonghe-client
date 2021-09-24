package vip.tuoyang.zhonghe.support;

import vip.tuoyang.zhonghe.config.properties.ServiceSystemProperties;
import vip.tuoyang.zhonghe.nettyclient.BroadcastClient;

/**
 * @author AlanSun
 * @date 2021/9/23 16:43
 */
public class MyServiceCallback implements ServiceCallback {
    public ServiceSystemProperties serviceSystemProperties;

    public MyServiceCallback(ServiceSystemProperties serviceSystemProperties) {
        this.serviceSystemProperties = serviceSystemProperties;
    }

    /**
     * 重连
     *
     * @param broadcastClient 通道
     */
    @Override
    public void reconnect(BroadcastClient broadcastClient) {
        broadcastClient.sendMessage("token:" + serviceSystemProperties.getSecret());
    }
}
