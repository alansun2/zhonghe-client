package vip.tuoyang.zhonghe.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import vip.tuoyang.base.util.AssertUtils;
import vip.tuoyang.zhonghe.bean.BroadcastInstallPath;
import vip.tuoyang.zhonghe.config.properties.ServiceSystemProperties;

import javax.annotation.PostConstruct;
import java.io.File;
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
    public void init() {
        serviceSystemProperties.valid();
        // 上报初始化数据
        final ZhongHeConfig zhongHeConfig = serviceSystemProperties.getZhongHeConfig();
        zhongHeConfig.valid();
        // 初始化安装路径
        this.initInstallPath();
    }

    private void initInstallPath() {
        String installDir;
        final String activeProfile = environment.getActiveProfiles()[0];
        final String path = ServiceConfig.class.getResource("/").getPath();
        if ("pro".equals(activeProfile)) {
            installDir = path.substring(6, path.lastIndexOf("zhonghe-broadcast") + 17);
        } else {
            installDir = serviceSystemProperties.getInstallDir();
        }

        List<File> files = new ArrayList<>();
        this.searchFile(files, installDir, "MiddleWare", ".exe", "中间件");
        String middleWarePath = files.get(0).getAbsolutePath();
        files.clear();
        this.searchFile(files, installDir, "服务器软件", ".exe", "nas");
        String nasPath = files.get(0).getAbsolutePath();
        files.clear();
        this.searchFile(files, installDir, "管理软件", ".exe", "管理软件");
        String managePath = files.get(0).getAbsolutePath();

        BroadcastInstallPath broadcastInstallPath = new BroadcastInstallPath();
        broadcastInstallPath.setInstallDir(installDir);
        broadcastInstallPath.setMiddleWarePath(middleWarePath);
        broadcastInstallPath.setNasPath(nasPath);
        broadcastInstallPath.setManagePath(managePath);
        serviceSystemProperties.setBroadcastInstallPath(broadcastInstallPath);
    }

    private void searchFile(List<File> files, String installDir, String prefix, String suffix, String des) {
        vip.tuoyang.base.util.FileUtils.searchFile(files, new File(installDir), 1, file -> {
            final String name = file.getName().toLowerCase();
            return name.startsWith(prefix.toLowerCase()) && name.endsWith(suffix);
        });
        AssertUtils.notEmpty(files, "未找到 " + des + " 安装地址");
    }
}
