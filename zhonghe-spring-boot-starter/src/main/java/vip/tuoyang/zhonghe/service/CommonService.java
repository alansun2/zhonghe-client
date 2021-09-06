package vip.tuoyang.zhonghe.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vip.tuoyang.zhonghe.bean.request.IpChangeRequest;
import vip.tuoyang.zhonghe.support.IpChangeCallback;

/**
 * @author AlanSun
 * @date 2021/9/3 14:23
 */
@Slf4j
@Service
public class CommonService {
    @Autowired(required = false)
    private IpChangeCallback ipChangeCallback;

    /**
     * ip 变化处理
     */
    public void ipChangeHandle(IpChangeRequest request) {
        log.info("ip 变化 request: [{}]", request);
        if (ipChangeCallback != null) {
            ipChangeCallback.handle(request);
        }
    }
}
