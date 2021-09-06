package vip.tuoyang.zhonghe.support;

import vip.tuoyang.zhonghe.bean.request.IpChangeRequest;

/**
 * @author AlanSun
 * @date 2021/9/3 14:17
 * <p>
 * ip 变化时的回调
 */
public interface IpChangeCallback {
    /**
     * 处理 ip 变化回调
     *
     * @param request {@link IpChangeRequest}
     */
    void handle(IpChangeRequest request);
}
