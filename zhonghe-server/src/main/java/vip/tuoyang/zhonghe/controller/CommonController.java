package vip.tuoyang.zhonghe.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
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
    public String uploadFile(HttpServletRequest request, @RequestParam("fileName") String fileName) throws IOException {
        return commonService.uploadFile(request, fileName);
    }

    /**
     * 获取基础信息
     *
     * @return {@link ServiceSystemProperties.ZhongHeConfig}
     */
    @GetMapping(value = "/get-config")
    public ServiceSystemProperties.ZhongHeConfig getConfig() {
        return serviceSystemProperties.getZhongHeConfig();
    }

    /**
     * 重启 nas 和中间件
     */
    @PostMapping(value = "/reboot")
    public void reboot() throws IOException {
        commonService.reboot();
    }
}
