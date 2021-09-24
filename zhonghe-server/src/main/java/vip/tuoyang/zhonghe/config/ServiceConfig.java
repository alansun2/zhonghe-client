package vip.tuoyang.zhonghe.config;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import vip.tuoyang.base.util.AssertUtils;
import vip.tuoyang.zhonghe.bean.BroadcastInstallPath;
import vip.tuoyang.zhonghe.config.properties.ServiceSystemProperties;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * @author AlanSun
 * @date 2021/9/4 16:29
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
public class ServiceConfig {
    @Autowired
    private ServiceSystemProperties serviceSystemProperties;
    @Autowired
    private Environment environment;

    @PostConstruct
    public void init() throws IOException {
        serviceSystemProperties.valid();
        // 上报初始化数据
        final ZhongHeConfig zhongHeConfig = serviceSystemProperties.getZhongHeConfig();
        zhongHeConfig.valid();
        // 初始化安装路径
        this.initInstallPath();
    }

    private void initInstallPath() throws IOException {
        String installDir;
        String installInfoPath;
        final String activeProfile = environment.getActiveProfiles()[0];
        final String path = ServiceConfig.class.getResource("/").getPath();
        if ("pro".equals(activeProfile)) {
            installDir = path.substring(6, path.lastIndexOf("zhonghe-broadcast") + 17);
            installInfoPath = installDir + "/install-info.txt";
        } else {
            installDir = serviceSystemProperties.getInstallDir();
            installInfoPath = path + "/install-info.txt";
        }
        log.info("installDir : [{}], installInfoPath: [{}]", installDir, installInfoPath);
        log.info("charset : [{}]", Charset.defaultCharset());

        BroadcastInstallPath broadcastInstallPath = null;
        try {
            broadcastInstallPath = JSON.parseObject(org.apache.commons.io.FileUtils.readFileToString(new File(installInfoPath), "UTF-8"), BroadcastInstallPath.class);
        } catch (Exception e) {
            if (!e.getMessage().contains("does not exist")) {
                throw e;
            }
        }
        if (broadcastInstallPath != null) {
            serviceSystemProperties.setBroadcastInstallPath(broadcastInstallPath);
            return;
        }

        String middleWarePath;
        String nasPath;
        List<File> files = new ArrayList<>();
        vip.tuoyang.base.util.FileUtils.searchFile(files, new File(installDir), 1, file -> {
            final String name = file.getName().toLowerCase();
            return name.startsWith("MiddleWare".toLowerCase()) && name.endsWith(".exe");
        });
        AssertUtils.notEmpty(files, "未找到中间件安装地址");
        middleWarePath = files.get(0).getAbsolutePath();
        files.clear();
        vip.tuoyang.base.util.FileUtils.searchFile(files, new File(installDir), 1, file -> {
            final String name = file.getName().toLowerCase();
            return name.startsWith("服务器软件".toLowerCase()) && name.endsWith(".exe");
        });
        AssertUtils.notEmpty(files, "未找到 nas 安装地址");
        nasPath = files.get(0).getAbsolutePath();

        broadcastInstallPath = new BroadcastInstallPath();
        broadcastInstallPath.setMiddleWarePath(middleWarePath);
        broadcastInstallPath.setNasPath(nasPath);
        org.apache.commons.io.FileUtils.writeStringToFile(new File(installInfoPath), JSON.toJSONString(broadcastInstallPath));
    }
}
