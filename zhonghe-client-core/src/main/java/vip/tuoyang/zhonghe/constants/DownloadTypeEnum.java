package vip.tuoyang.zhonghe.constants;

/**
 * @author AlanSun
 * @date 2021/8/27 22:44
 * <p>
 * 下载类型
 **/
public enum DownloadTypeEnum {
    /**
     * 终端分组
     */
    TERMINAL_GROUP("03"),
    /**
     * 媒体文件
     */
    MEDIA_FILE("04"),
    NULL("null"),
    ;

    private final String value;

    DownloadTypeEnum(String value) {
        this.value = value;
    }

    public static DownloadTypeEnum valueBy(String para) {
        switch (para) {
            case "03":
                return TERMINAL_GROUP;
            case "04":
                return MEDIA_FILE;
            default:
                return NULL;
        }
    }
}
