package vip.tuoyang.zhonghe.service;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vip.tuoyang.zhonghe.config.properties.ServiceSystemProperties;
import vip.tuoyang.zhonghe.utils.ConvertCode;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

/**
 * @author AlanSun
 * @date 2021/9/3 11:44
 */
@Service
public class CommonService {
    @Autowired
    private ServiceSystemProperties serviceSystemProperties;

    /**
     * 文件上传
     *
     * @return 文件地址
     */
    public String uploadFile(HttpServletRequest request, String fileName) throws IOException {
        final ServletInputStream inputStream = request.getInputStream();
        final String filePath = serviceSystemProperties.getFileDir() + fileName;
        FileUtils.copyInputStreamToFile(inputStream, new File(filePath));
        return filePath;
    }
}
