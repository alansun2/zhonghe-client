package vip.tuoyang.zhonghe.support;

import vip.tuoyang.zhonghe.bean.ZhongHeDto;
import vip.tuoyang.zhonghe.config.ZhongHeConfig;
import vip.tuoyang.zhonghe.nettyclient.BroadcastClient;

/**
 * @author AlanSun
 * @date 2021/9/23 16:43
 */
public class MyServiceCallback implements ServiceCallback {

    /**
     * 重连
     *
     * @param broadcastClient 通道
     */
    @Override
    public void reconnect(BroadcastClient broadcastClient) {
        ZhongHeDto<ZhongHeConfig> zhongHeBaseRequest = new ZhongHeDto<>();
        zhongHeBaseRequest.setCommand((byte) 14);
        broadcastClient.sendMessage(zhongHeBaseRequest);
    }
}
