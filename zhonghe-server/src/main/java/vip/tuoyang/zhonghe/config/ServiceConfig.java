package vip.tuoyang.zhonghe.config;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.config.Task;
import vip.tuoyang.base.exception.BizException;
import vip.tuoyang.base.util.AssertUtils;
import vip.tuoyang.base.util.HttpClientUtils;
import vip.tuoyang.base.util.IpUtils;
import vip.tuoyang.base.util.StringUtils;
import vip.tuoyang.base.util.bean.HttpParams;
import vip.tuoyang.zhonghe.bean.request.BroadcastInstallPath;
import vip.tuoyang.zhonghe.config.properties.ServiceSystemProperties;
import vip.tuoyang.zhonghe.schedule.IpSchedule;
import vip.tuoyang.zhonghe.service.CommonService;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @author AlanSun
 * @date 2021/9/4 16:29
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
public class ServiceConfig implements SchedulingConfigurer {
    @Autowired
    private IpSchedule ipSchedule;
    @Autowired
    private ServiceSystemProperties serviceSystemProperties;
    @Autowired
    private Environment environment;

    /**
     * Callback allowing a {@link TaskScheduler
     * TaskScheduler} and specific {@link Task Task}
     * instances to be registered against the given the {@link ScheduledTaskRegistrar}.
     *
     * @param taskRegistrar the registrar to be configured.
     */
    @Override
    public void configureTasks(@NonNull ScheduledTaskRegistrar taskRegistrar) {
        if (serviceSystemProperties.isIpReportSwitch()) {
            taskRegistrar.addFixedDelayTask(ipSchedule::ipChangeListen, 10000);
        }
    }

    @PostConstruct
    public void init() throws IOException {
        // 初始化安装路径
        this.initInstallPath();

        // 上报初始化数据
        final ServiceSystemProperties.ZhongHeConfig zhongHeConfig = serviceSystemProperties.getZhongHeConfig();
        zhongHeConfig.valid();

        // 优先获取设置的 ip
        final String publicIp = IpUtils.getPublicIp();
        AssertUtils.notNull(publicIp, "获取公网ip失败");
        if (zhongHeConfig.getNasIp() == null) {
            zhongHeConfig.setNasIp(publicIp);
        }
        if (zhongHeConfig.getMiddleWareIp() == null) {
            zhongHeConfig.setMiddleWareIp(publicIp);
        }
        zhongHeConfig.setFileUploadUrl("http://" + zhongHeConfig.getMiddleWareIp() + ":" + environment.getProperty("server.port") + serviceSystemProperties.getFileUploadPath());

        List<Header> headers = new ArrayList<>();
        headers.add(new BasicHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE));
        headers.add(new BasicHeader("secret", serviceSystemProperties.getSecret()));
        final HttpParams httpParams = HttpParams.builder()
                .url(serviceSystemProperties.getServerUrl() + serviceSystemProperties.getPath().getServerInit())
                .headers(headers)
                .httpEntity(new StringEntity(JSON.toJSONString(zhongHeConfig), StandardCharsets.UTF_8)).build();
        final HttpResponse httpResponse;
        try {
            httpResponse = HttpClientUtils.doPost(httpParams);
        } catch (IOException e) {
            log.error("启动失败,未连接到设备服务器", e);
            throw new BizException("启动失败,未连接到设备服务器");
        }
        if (httpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
            throw new BizException("启动失败,未连接到设备服务器");
        } else {
            final String res = EntityUtils.toString(httpResponse.getEntity());
            if (StringUtils.isNotEmpty(res)) {
                throw new BizException("启动失败，errorMsg: " + res);
            }
        }
    }

    private void initInstallPath() throws IOException {
        String path2 = ServiceConfig.class.getResource("/").getPath();
        BroadcastInstallPath broadcastInstallPath = null;
        try {
            broadcastInstallPath = JSON.parseObject(org.apache.commons.io.FileUtils.readFileToString(new File(path2), "UTF-8"), BroadcastInstallPath.class);
        } catch (Exception e) {
            if (!e.getMessage().contains("NOT FOUND")) {
                throw e;
            }
        }
        if (broadcastInstallPath != null) {
            serviceSystemProperties.setBroadcastInstallPath(broadcastInstallPath);
            return;
        }

        String installDir = serviceSystemProperties.getInstallDir();
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(installDir)) {
            installDir = CommonService.class.getResource("").getPath().substring(0, 3);
        }
        String middleWarePath;
        String nasPath;
        List<File> files = new ArrayList<>();
        vip.tuoyang.base.util.FileUtils.searchFile(files, new File(installDir), 2, file -> {
            final String name = file.getName().toLowerCase();
            return name.startsWith("MiddleWare".toLowerCase()) && name.endsWith(".exe");
        });
        AssertUtils.notEmpty(files, "未找到中间件安装地址");
        middleWarePath = files.get(0).getAbsolutePath();
        files.clear();
        vip.tuoyang.base.util.FileUtils.searchFile(files, new File(installDir), 2, file -> {
            final String name = file.getName().toLowerCase();
            return name.startsWith("服务器软件".toLowerCase()) && name.endsWith(".exe");
        });
        AssertUtils.notEmpty(files, "未找到 nas 安装地址");
        nasPath = files.get(0).getAbsolutePath();

        broadcastInstallPath = new BroadcastInstallPath();
        broadcastInstallPath.setMiddleWarePath(middleWarePath);
        broadcastInstallPath.setNasPath(nasPath);
        org.apache.commons.io.FileUtils.writeStringToFile(new File(path2), JSON.toJSONString(broadcastInstallPath));
    }
}
