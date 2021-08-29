package vip.tuoyang.zhonghe.service.downloadhandler;

/**
 * @author AlanSun
 * @date 2021/8/27 8:50
 */
public class GroupDataDownloadHandler implements DownLoadResultHandler {

    private GroupDataDownloadHandler() {
    }

    private static final GroupDataDownloadHandler HANDLER = new GroupDataDownloadHandler();

    public static GroupDataDownloadHandler getInstance() {
        return HANDLER;
    }

    /**
     * 返回数据处理
     *
     * @param contentStr contentStr
     */
    @Override
    public void handler(String para, String contentStr) {
    }
}
