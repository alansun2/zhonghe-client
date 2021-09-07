package vip.tuoyang.zhonghe.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vip.tuoyang.zhonghe.bean.request.IpChangeRequest;
import vip.tuoyang.zhonghe.config.ZhongHeConfig;
import vip.tuoyang.zhonghe.support.ServiceZhongHeCallback;

/**
 * @author AlanSun
 * @date 2021/9/3 14:23
 */
@Slf4j
@Service
public class CommonService {
    @Autowired(required = false)
    private ServiceZhongHeCallback ipChangeCallback;

    /**
     * 服务初始化
     *
     * @param zhongHeConfig {@link ZhongHeConfig}
     */
    public void serverInit(ZhongHeConfig zhongHeConfig) {
        ipChangeCallback.serverInit(zhongHeConfig);
    }

    /**
     * ip 变化处理
     */
    public void ipChangeHandle(IpChangeRequest request) {
        log.info("ip 变化 request: [{}]", request);
        if (ipChangeCallback != null) {
            ipChangeCallback.ipChange(request);
        }
    }
}
