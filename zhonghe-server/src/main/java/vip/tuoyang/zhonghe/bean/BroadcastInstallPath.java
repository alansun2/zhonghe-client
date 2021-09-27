package vip.tuoyang.zhonghe.bean;

import lombok.Getter;
import lombok.Setter;

/**
 * @author AlanSun
 * @date 2021/9/17 16:42
 */
@Getter
@Setter
public class BroadcastInstallPath {
    /**
     * 安装目录
     */
    private String installDir;
    /**
     * 中河中间件路径
     */
    private String middleWarePath;
    /**
     * nas 路径
     */
    private String nasPath;
    /**
     * 管理软件路径
     */
    private String managePath;
    /**
     * 自己的jar文件的路径
     */
    private String myselfPath;
    /**
     * 自己的启动文件的路径
     */
    private String myselfRestartPath;
}
