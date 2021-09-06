package vip.tuoyang.zhonghe;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import vip.tuoyang.zhonghe.bean.request.IpChangeRequest;
import vip.tuoyang.zhonghe.support.IpChangeCallback;

/**
 * @author AlanSun
 * @date 2021/9/4 12:39
 */
@Slf4j
@Component
public class IpChangeCallbackImpl implements IpChangeCallback {
    /**
     * 处理 ip 变化回调
     *
     * @param request {@link IpChangeRequest}
     */
    @Override
    public void handle(IpChangeRequest request) {
        log.info("接收到参数: [{}]", request);
    }
}
