package vip.tuoyang.zhonghe.support;

import vip.tuoyang.zhonghe.bean.ResultInternal;
import vip.tuoyang.zhonghe.bean.response.ZhongHeResponse;
import vip.tuoyang.zhonghe.constants.CmdEnum;
import vip.tuoyang.zhonghe.utils.ServiceUtils;

/**
 * @author AlanSun
 * @date 2021/8/29 16:09
 */
public class SendStateHandler {
    public void handle(ZhongHeResponse zhongHeResponse, String label) {
        final ResultInternal resultInternal = SyncResultSupport.labelResultInternal.get(label);
        if (resultInternal != null) {
            final CmdEnum cmdEnum = resultInternal.getZhongHeResponse().getCmdEnum();
            if (cmdEnum == CmdEnum.PRO_TIMING_TASK) {
                resultInternal.setData(zhongHeResponse.getContent().substring(8, 10));
                SyncResultSupport.labelResultCountDownMap.get(label).countDown();
            } else if (cmdEnum == CmdEnum.UPLOAD_MEDIA_FILE) {
                final boolean mediaFileUploadComplete = this.isMediaFileUploadComplete();
                if (mediaFileUploadComplete) {
                    resultInternal.setData(this.getMediaFileNo());
                    SyncResultSupport.labelResultCountDownMap.get(label).countDown();
                }
            }
        }
    }

    private final ZhongHeResponse zhongHeResponse;

    public SendStateHandler(ZhongHeResponse zhongHeResponse) {
        this.zhongHeResponse = zhongHeResponse;
    }

    /**
     * 是否在线
     *
     * @return true: 在线; false：下线
     */
    public boolean isOnline() {
        return "01AA".equals(zhongHeResponse.getContent().substring(0, 4));
    }

    /**
     * 文件上传是否成功
     *
     * @return true: 成功; false: 失败
     */
    public boolean isMediaFileUploadComplete() {
        return "00".equals(zhongHeResponse.getContent().substring(4, 6));
    }

    /**
     * 获取文件上传进度
     *
     * @return 45 表示 45%
     */
    public int mediaFileProcess() {
        return Integer.parseInt(zhongHeResponse.getContent().substring(12, 14), 16);
    }

    /**
     * 获取文件编号
     *
     * @return 文件编号
     */
    public String getMediaFileNo() {
        return ServiceUtils.changeOrder(zhongHeResponse.getContent().substring(16, 20), 2);
    }
}
