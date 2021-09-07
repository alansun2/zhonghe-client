package vip.tuoyang.zhonghe.support;

import vip.tuoyang.zhonghe.bean.request.IpChangeRequest;
import vip.tuoyang.zhonghe.config.ZhongHeConfig;

/**
 * @author AlanSun
 * @date 2021/9/3 14:17
 * <p>
 * ip 变化时的回调
 */
public interface ServiceZhongHeCallback {
    /**
     * 处理 ip 变化回调
     *
     * @param request {@link IpChangeRequest}
     */
    void ipChange(IpChangeRequest request);

    /**
     * 服务初始化
     *
     * @param zhongHeConfig {@link ZhongHeConfig}
     */
    void serverInit(ZhongHeConfig zhongHeConfig);
}
