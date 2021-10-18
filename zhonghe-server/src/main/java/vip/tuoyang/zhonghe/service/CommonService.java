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
import vip.tuoyang.base.util.AssertUtils;
import vip.tuoyang.base.util.DateUtils;
import vip.tuoyang.zhonghe.bean.BroadcastInstallPath;
import vip.tuoyang.zhonghe.bean.SoftInfo;
import vip.tuoyang.zhonghe.bean.ZhongHeDto;
import vip.tuoyang.zhonghe.bean.request.FileUpdate;
import vip.tuoyang.zhonghe.bean.request.MyselfUpdate;
import vip.tuoyang.zhonghe.bean.request.StateRequest;
import vip.tuoyang.zhonghe.bean.request.ZhongHeSoftUpdateRequest;
import vip.tuoyang.zhonghe.config.properties.ServiceSystemProperties;
import vip.tuoyang.zhonghe.nettyclient.BroadcastClient;
import vip.tuoyang.zhonghe.utils.ServiceUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
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
    @Autowired
    private BroadcastClient broadcastClient;

    private ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(5);

    /**
     * 重启 nas 和中间件
     */
    public void reboot() throws IOException {
        final BroadcastInstallPath broadcastInstallPath = serviceSystemProperties.getBroadcastInstallPath();
        this.rebootInternal(8607, broadcastInstallPath.getMiddleWarePath());
        this.rebootInternal(8200, broadcastInstallPath.getNasPath());
        taskExecutor.execute(() -> {
            LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(100));
            zhongHeClient.state();
        });
    }

    private void stop(int port) throws IOException {
        final Process exec = Runtime.getRuntime().exec("cmd /c netstat -ano | findstr " + port);

        final InputStream inputStream = exec.getInputStream();
        final String resultStr = IOUtils.toString(inputStream);
        if (StringUtils.isNotEmpty(resultStr)) {
            this.exec("tskill " + StringUtils.split(resultStr, SeparatorConstants.SPACE)[3]);
        }
        exec.destroy();
    }

    private void start(String processPath) throws IOException {
        this.exec("cmd /c start " + processPath);
    }

    private void rebootInternal(int port, String processPath) throws IOException {
        this.stop(port);
        this.start(processPath);
    }

    public void generatorTimer() throws IOException {
        // 生成定时任务
        this.exec("schtasks /Create /SC MINUTE /TN 广播定时检查 /ST 03:05 /ET 23:59 /TR " + serviceSystemProperties.getBroadcastInstallPath().getInstallDir() + "/schedule-check.bat");
        this.exec("schtasks /Create /SC DAILY /TN 广播定时重启 /ST 01:05 /TR " + serviceSystemProperties.getBroadcastInstallPath().getInstallDir() + "/restart.bat");
        this.exec("schtasks /Create /SC ONSTART /TN 广播自启 /TR " + serviceSystemProperties.getBroadcastInstallPath().getInstallDir() + "/restart.bat");
    }

    /**
     * 软件更新
     */
    public void update(ZhongHeSoftUpdateRequest softUpdateRequest) throws IOException {
        log.info("软件更新开始");
        final String version = softUpdateRequest.getVersion();
        AssertUtils.notBlank(version, "version blank error");
        final BroadcastInstallPath broadcastInstallPath = serviceSystemProperties.getBroadcastInstallPath();
        boolean startFlag = false;
        try {
            File softInfoPath = ServiceUtils.getSoftInfoPath(broadcastInstallPath.getInstallDir());
            if (softInfoPath.exists()) {
                final SoftInfo softInfo = JSON.parseObject(FileUtils.readFileToString(softInfoPath, Charset.defaultCharset().toString()), SoftInfo.class);
                if (StringUtils.isEmpty(softInfo.getVersion()) || !softInfo.getVersion().equals(version)) {
                    this.copy(softInfo, softUpdateRequest, broadcastInstallPath);
                    startFlag = true;
                }
            } else {
                SoftInfo softInfo = new SoftInfo();
                this.copy(softInfo, softUpdateRequest, broadcastInstallPath);
                startFlag = true;
            }
        } catch (Exception e) {
            this.generatorTimer();
        } finally {
            if (startFlag) {
                try {
                    this.start(broadcastInstallPath.getMiddleWarePath());
                    this.start(broadcastInstallPath.getNasPath());
                    this.generatorTimer();
                    taskExecutor.execute(() -> {
                        // 为了连上中间件
                        LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(100));
                        zhongHeClient.state();
                    });
                } catch (IOException e) {
                    log.error("更新失败,重启失败", e);
                }
            }
        }
    }

    private void copy(SoftInfo softInfo, ZhongHeSoftUpdateRequest softUpdateRequest, BroadcastInstallPath broadcastInstallPath) throws IOException {
        // 删除定时
        this.exec(broadcastInstallPath.getInstallDir() + "/delete-timer.bat");
        this.stop(8200);
        this.stop(8607);

        if (StringUtils.isNotEmpty(softUpdateRequest.getNasUrl())) {
            FileUtils.copyURLToFile(new URL(softUpdateRequest.getNasUrl()), new File(broadcastInstallPath.getNasPath()));
        }
        if (StringUtils.isNotEmpty(softUpdateRequest.getMiddlewareUrl())) {
            FileUtils.copyURLToFile(new URL(softUpdateRequest.getMiddlewareUrl()), new File(broadcastInstallPath.getMiddleWarePath()));
        }
        try {
            if (StringUtils.isNotEmpty(softUpdateRequest.getManageUrl())) {
                FileUtils.copyURLToFile(new URL(softUpdateRequest.getManageUrl()), new File(broadcastInstallPath.getInstallDir() + "/manage/管理软件-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern(DateUtils.DATE_LONG_FORMAT)) + ".exe"));
            }
        } catch (Exception e) {
            log.error("管理软件更新失败", e);
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
    public void updateMyself(MyselfUpdate myselfUpdate) throws IOException {
        log.info("myself 软件更新开始");
        myselfUpdate.valid();
        final String version = myselfUpdate.getVersion();

        final BroadcastInstallPath broadcastInstallPath = serviceSystemProperties.getBroadcastInstallPath();
        boolean startFlag = false;
        try {
            File softInfoPath = ServiceUtils.getSoftInfoPath(broadcastInstallPath.getInstallDir());
            if (softInfoPath.exists()) {
                final SoftInfo softInfo = JSON.parseObject(FileUtils.readFileToString(softInfoPath, Charset.defaultCharset().toString()), SoftInfo.class);
                if (StringUtils.isEmpty(softInfo.getMyselfVersion()) || !softInfo.getMyselfVersion().equals(version)) {
                    this.downloadMyself(softInfo, myselfUpdate, broadcastInstallPath);
                    startFlag = true;
                }
            } else {
                SoftInfo softInfo = new SoftInfo();
                this.downloadMyself(softInfo, myselfUpdate, broadcastInstallPath);
                startFlag = true;
            }
        } catch (Exception e) {
            this.generatorTimer();
        } finally {
            if (startFlag) {
                try {
                    this.start(broadcastInstallPath.getMyselfUpdatePath());
                } catch (IOException e) {
                    log.error("更新失败,重启失败", e);
                }
            }
        }
    }

    private void downloadMyself(SoftInfo softInfo, MyselfUpdate myselfUpdate, BroadcastInstallPath broadcastInstallPath) throws IOException {
        // 关闭软件
        this.exec(broadcastInstallPath.getInstallDir() + "/delete-timer.bat");
        if (StringUtils.isNotEmpty(myselfUpdate.getMyselfUrl())) {
            // 下载文件
            FileUtils.copyURLToFile(new URL(myselfUpdate.getMyselfUrl()), new File(broadcastInstallPath.getInstallDir() + "/jszn-middleware/zhonghe-server.jar"));
        }
        softInfo.setMyselfVersion(myselfUpdate.getVersion());
        File softInfoPath = ServiceUtils.getSoftInfoPath(broadcastInstallPath.getInstallDir());
        FileUtils.writeStringToFile(softInfoPath, JSON.toJSONString(softInfo));
    }

    /**
     * 文件更新
     *
     * @param fileUpdate {@link FileUpdate}
     */
    public void updateFile(FileUpdate fileUpdate) throws IOException {
        log.info("文件更新开始");
        final BroadcastInstallPath broadcastInstallPath = serviceSystemProperties.getBroadcastInstallPath();
        final String installDir = broadcastInstallPath.getInstallDir();
        FileUtils.copyURLToFile(new URL(fileUpdate.getFileUrl()), new File(installDir + "/" + fileUpdate.getFileName()));
    }

    private void exec(String command) throws IOException {
        final Process exec = Runtime.getRuntime().exec(command);
        scheduledExecutorService.schedule(exec::destroy, 5, TimeUnit.SECONDS);
    }
}
