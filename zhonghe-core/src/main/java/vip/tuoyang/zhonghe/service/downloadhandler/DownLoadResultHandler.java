package vip.tuoyang.zhonghe.service.downloadhandler;

/**
 * @author AlanSun
 * @date 2021/8/27 8:50
 */
public interface DownLoadResultHandler {
    /**
     * 返回数据处理
     *
     * @param contentStr contentStr
     */
    void handler(String para, String contentStr);
}
