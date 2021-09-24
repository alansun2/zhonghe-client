package vip.tuoyang.zhonghe.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vip.tuoyang.base.constants.SeparatorConstants;
import vip.tuoyang.zhonghe.bean.BroadcastInstallPath;
import vip.tuoyang.zhonghe.config.properties.ServiceSystemProperties;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author AlanSun
 * @date 2021/9/3 11:44
 */
@Slf4j
@Service
public class CommonService {
    @Autowired
    private ServiceSystemProperties serviceSystemProperties;

    /**
     * 重启 nas 和中间件
     */
    public void reboot() throws IOException {
        final BroadcastInstallPath broadcastInstallPath = serviceSystemProperties.getBroadcastInstallPath();
        final Runtime runtime = Runtime.getRuntime();
        this.rebootInternal(runtime, 8607, broadcastInstallPath.getMiddleWarePath());
        this.rebootInternal(runtime, 8200, broadcastInstallPath.getNasPath());
    }

    private void rebootInternal(Runtime runtime, int port, String processPath) throws IOException {
        final Process exec = runtime.exec("cmd /c netstat -ano | findstr " + port);
        final InputStream inputStream = exec.getInputStream();
        final String resultStr = IOUtils.toString(inputStream);
        runtime.exec("tskill " + StringUtils.split(resultStr, SeparatorConstants.SPACE)[3]);
        runtime.exec("cmd /c start " + processPath);
        inputStream.close();
        exec.destroy();
    }
}
