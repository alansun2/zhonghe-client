package vip.tuoyang.zhonghe.utils;

import org.springframework.stereotype.Component;

import java.io.File;

/**
 * @author AlanSun
 * @date 2021/9/27 9:54
 */
@Component
public class ServiceUtils {
    /**
     * 获取软件信息文件存在地址
     *
     * @param installDir 软件安装地址
     * @return 软件信息文件存在地址
     */
    public static File getSoftInfoPath(String installDir) {
        return new File(installDir + "/jszn-middleware/soft-info");
    }
}
