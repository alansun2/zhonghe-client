package vip.tuoyang.zhonghe.support;

import vip.tuoyang.zhonghe.bean.request.StateRequest;
import vip.tuoyang.zhonghe.config.ZhongHeConfig;

/**
 * @author AlanSun
 * @date 2021/9/3 14:17
 * <p>
 * ip 变化时的回调
 */
public interface ServiceZhongHeCallback {
    /**
     * 服务初始化
     *
     * @param zhongHeConfig {@link ZhongHeConfig}
     */
    void serverInit(ZhongHeConfig zhongHeConfig);

    /**
     * 广播服务状态修改时回调
     *
     * @param stateRequest {@link StateRequest}
     */
    void stateChange(StateRequest stateRequest);
}
