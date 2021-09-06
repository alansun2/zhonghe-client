package vip.tuoyang.zhonghe.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vip.tuoyang.base.util.AssertUtils;
import vip.tuoyang.zhonghe.config.properties.ServiceSystemProperties;
import vip.tuoyang.zhonghe.service.CommonService;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author AlanSun
 * @date 2021/9/3 11:33
 */
@RestController
@RequestMapping(value = "/common")
public class CommonController {
    @Autowired
    private CommonService commonService;
    @Autowired
    private ServiceSystemProperties serviceSystemProperties;

    /**
     * 上传文件
     *
     * @return 文件地址
     */
    @PostMapping(value = "/upload-file")
    public String uploadFile(HttpServletRequest request, @RequestParam("fileName") String fileName, @RequestHeader("secret") String secret) throws IOException {
        AssertUtils.isTrue(secret.equals(serviceSystemProperties.getSecret()), "别乱调接口");
        return commonService.uploadFile(request, fileName);
    }
}
