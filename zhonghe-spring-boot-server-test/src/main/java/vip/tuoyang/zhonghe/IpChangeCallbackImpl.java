package vip.tuoyang.zhonghe;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import vip.tuoyang.zhonghe.bean.request.IpChangeRequest;
import vip.tuoyang.zhonghe.config.ZhongHeConfig;
import vip.tuoyang.zhonghe.support.ServiceZhongHeCallback;

/**
 * @author AlanSun
 * @date 2021/9/4 12:39
 */
@Slf4j
@Component
public class IpChangeCallbackImpl implements ServiceZhongHeCallback {
    /**
     * 处理 ip 变化回调
     *
     * @param request {@link IpChangeRequest}
     */
    @Override
    public void ipChange(IpChangeRequest request) {
        log.info("接收到参数: [{}]", request);
    }

    /**
     * 服务初始化
     *
     * @param zhongHeConfig {@link ZhongHeConfig
     */
    @Override
    public void serverInit(ZhongHeConfig zhongHeConfig) {
        log.info("接收到参数: zhongHeConfig: [{}]", zhongHeConfig);
    }
}
