package vip.tuoyang.zhonghe.support;

import vip.tuoyang.zhonghe.nettyclient.BroadcastClient;

/**
 * @author AlanSun
 * @date 2021/9/23 16:42
 */
public interface ServiceCallback {
    /**
     * ιθΏ
     *
     * @param broadcastClient ιι
     */
    void reconnect(BroadcastClient broadcastClient);
}
