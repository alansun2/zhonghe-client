package vip.tuoyang.zhonghe.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vip.tuoyang.base.util.AssertUtils;
import vip.tuoyang.zhonghe.bean.request.IpChangeRequest;
import vip.tuoyang.zhonghe.config.properties.ServiceSystemProperties;
import vip.tuoyang.zhonghe.service.CommonService;

/**
 * @author AlanSun
 * @date 2021/9/3 13:55
 */
@RestController
@RequestMapping(value = "/common")
public class CommonController {
    @Autowired
    private CommonService commonService;
    @Autowired
    private ServiceSystemProperties serviceSystemProperties;

    /**
     * 公网 ip
     *
     * @param request {@link IpChangeRequest}
     */
    @PostMapping(value = "/ip-report")
    public void ipReport(@RequestBody IpChangeRequest request, @RequestHeader() String secret) {
        AssertUtils.isTrue(serviceSystemProperties.getSecret().equals(secret), "密码不正确");
        commonService.ipChangeHandle(request);
    }
}
