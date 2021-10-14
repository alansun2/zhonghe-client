package vip.tuoyang.zhonghe.service;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import vip.tuoyang.base.constants.SeparatorConstants;
import vip.tuoyang.base.exception.BizException;
import vip.tuoyang.base.util.AssertUtils;
import vip.tuoyang.zhonghe.bean.BroadcastInstallPath;
import vip.tuoyang.zhonghe.bean.SoftInfo;
import vip.tuoyang.zhonghe.bean.request.FileUpdate;
import vip.tuoyang.zhonghe.bean.request.MyselfUpdate;
import vip.tuoyang.zhonghe.bean.request.ZhongHeSoftUpdateRequest;
import vip.tuoyang.zhonghe.config.properties.ServiceSystemProperties;
import vip.tuoyang.zhonghe.utils.ServiceUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * @author AlanSun
 * @date 2021/9/3 11:44
 */
@Lazy
@Slf4j
@Service
public class CommonService {
    @Autowired
    private ZhongHeClient zhongHeClient;
    @Autowired
    private ServiceSystemProperties serviceSystemProperties;
    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    /**
     * 重启 nas 和中间件
     */
    public void reboot() throws IOException {
        final BroadcastInstallPath broadcastInstallPath = serviceSystemProperties.getBroadcastInstallPath();
        final Runtime runtime = Runtime.getRuntime();
        this.rebootInternal(runtime, 8607, broadcastInstallPath.getMiddleWarePath());
        this.rebootInternal(runtime, 8200, broadcastInstallPath.getNasPath());
        taskExecutor.execute(() -> {
            LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(100));
            zhongHeClient.state();
        });
    }

    private void stop(Runtime runtime, int port) throws IOException {
        final Process exec = runtime.exec("cmd /c netstat -ano | findstr " + port);
        final InputStream inputStream = exec.getInputStream();
        final String resultStr = IOUtils.toString(inputStream);
        if (StringUtils.isNotEmpty(resultStr)) {
            runtime.exec("tskill " + StringUtils.split(resultStr, SeparatorConstants.SPACE)[3]);
        }
    }

    private void start(Runtime runtime, String processPath) throws IOException {
        runtime.exec("cmd /c start " + processPath);
    }

    private void rebootInternal(Runtime runtime, int port, String processPath) throws IOException {
        this.stop(runtime, port);
        this.start(runtime, processPath);
    }

    /**
     * 软件更新
     */
    public void update(ZhongHeSoftUpdateRequest softUpdateRequest) {
        final String version = softUpdateRequest.getVersion();
        AssertUtils.notBlank(version, "version blank error");
        final BroadcastInstallPath broadcastInstallPath = serviceSystemProperties.getBroadcastInstallPath();
        Runtime runtime = null;
        boolean startFlag = false;
        try {
            runtime = Runtime.getRuntime();
            File softInfoPath = ServiceUtils.getSoftInfoPath(broadcastInstallPath.getInstallDir());
            if (softInfoPath.exists()) {
                final SoftInfo softInfo = JSON.parseObject(FileUtils.readFileToString(softInfoPath, Charset.defaultCharset().toString()), SoftInfo.class);
                if (!softInfo.getVersion().equals(version)) {
                    this.copy(softInfo, runtime, softUpdateRequest, broadcastInstallPath);
                    startFlag = true;
                }
            } else {
                SoftInfo softInfo = new SoftInfo();
                this.copy(softInfo, runtime, softUpdateRequest, broadcastInstallPath);
                startFlag = true;
            }
        } catch (Throwable t) {
            log.error("更新失败", t);
            throw new BizException("更新失败");
        } finally {
            if (startFlag) {
                if (runtime != null) {
                    try {
                        this.start(runtime, broadcastInstallPath.getMiddleWarePath());
                        this.start(runtime, broadcastInstallPath.getNasPath());
                        taskExecutor.execute(() -> {
                            LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(100));
                            zhongHeClient.state();
                        });
                    } catch (IOException e) {
                        log.error("更新失败,重启失败", e);
                    }
                }
            }
        }
    }

    private void copy(SoftInfo softInfo, Runtime runtime, ZhongHeSoftUpdateRequest softUpdateRequest, BroadcastInstallPath broadcastInstallPath) throws IOException {
        this.stop(runtime, 8200);
        this.stop(runtime, 8607);

        if (StringUtils.isNotEmpty(softUpdateRequest.getNasUrl())) {
            FileUtils.copyURLToFile(new URL(softUpdateRequest.getNasUrl()), new File(broadcastInstallPath.getNasPath()));
        }
        if (StringUtils.isNotEmpty(softUpdateRequest.getManageUrl())) {
            FileUtils.copyURLToFile(new URL(softUpdateRequest.getManageUrl()), new File(broadcastInstallPath.getManagePath()));
        }
        if (StringUtils.isNotEmpty(softUpdateRequest.getMiddlewareUrl())) {
            FileUtils.copyURLToFile(new URL(softUpdateRequest.getMiddlewareUrl()), new File(broadcastInstallPath.getMiddleWarePath()));
        }
        softInfo.setVersion(softUpdateRequest.getVersion());
        File softInfoPath = ServiceUtils.getSoftInfoPath(broadcastInstallPath.getInstallDir());
        FileUtils.writeStringToFile(softInfoPath, JSON.toJSONString(softInfo));
    }

    /**
     * 更新自己
     *
     * @param myselfUpdate {@link MyselfUpdate}
     */
    public void updateMyself(MyselfUpdate myselfUpdate) {
        myselfUpdate.valid();
        final String version = myselfUpdate.getVersion();

        final BroadcastInstallPath broadcastInstallPath = serviceSystemProperties.getBroadcastInstallPath();
        Runtime runtime = null;
        boolean startFlag = false;
        try {
            runtime = Runtime.getRuntime();
            File softInfoPath = ServiceUtils.getSoftInfoPath(broadcastInstallPath.getInstallDir());
            if (softInfoPath.exists()) {
                final SoftInfo softInfo = JSON.parseObject(FileUtils.readFileToString(softInfoPath, Charset.defaultCharset().toString()), SoftInfo.class);
                if (!softInfo.getVersion().equals(version)) {
                    this.downloadMyself(softInfo, myselfUpdate, broadcastInstallPath);
                    startFlag = true;
                }
            } else {
                SoftInfo softInfo = new SoftInfo();
                this.downloadMyself(softInfo, myselfUpdate, broadcastInstallPath);
                startFlag = true;
            }
        } catch (Throwable t) {
            log.error("更新失败", t);
            throw new BizException("更新失败");
        } finally {
            if (startFlag) {
                if (runtime != null) {
                    try {
                        runtime.exec(broadcastInstallPath.getMyselfRestartPath());
                    } catch (IOException e) {
                        log.error("更新失败,重启失败", e);
                    }
                }
            }
        }
    }

    private void downloadMyself(SoftInfo softInfo, MyselfUpdate myselfUpdate, BroadcastInstallPath broadcastInstallPath) throws IOException {
        if (StringUtils.isNotEmpty(myselfUpdate.getMyselfUrl())) {
            FileUtils.copyURLToFile(new URL(myselfUpdate.getMyselfUrl()), new File(broadcastInstallPath.getMyselfPath()));
        }
        softInfo.setVersion(myselfUpdate.getVersion());
        File softInfoPath = ServiceUtils.getSoftInfoPath(broadcastInstallPath.getInstallDir());
        FileUtils.writeStringToFile(softInfoPath, JSON.toJSONString(softInfo));
    }

    /**
     * 文件更新
     *
     * @param fileUpdate {@link FileUpdate}
     */
    public void updateFile(FileUpdate fileUpdate) throws IOException {
        final BroadcastInstallPath broadcastInstallPath = serviceSystemProperties.getBroadcastInstallPath();
        final String installDir = broadcastInstallPath.getInstallDir();
        FileUtils.copyURLToFile(new URL(fileUpdate.getFileUrl()), new File(installDir + "/" + fileUpdate.getFileName()));
    }
}
