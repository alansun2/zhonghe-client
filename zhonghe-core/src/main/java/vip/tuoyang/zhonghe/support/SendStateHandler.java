package vip.tuoyang.zhonghe.support;

import vip.tuoyang.zhonghe.bean.ResultInternal;
import vip.tuoyang.zhonghe.bean.response.ZhongHeResponse;
import vip.tuoyang.zhonghe.constants.CmdEnum;
import vip.tuoyang.zhonghe.constants.StateEnum;
import vip.tuoyang.zhonghe.utils.ZhongHeUtils;

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
                final String id = zhongHeResponse.getContent().substring(8, 10);
                // 这里不处理 00, 因为有时候第一个state返回的是 00
                if (!"00".equals(id)) {
                    resultInternal.setData(id);
                    SyncResultSupport.getLabelResultCountDown(label).countDown();
                }
            } else if (cmdEnum == CmdEnum.UPLOAD_MEDIA_FILE) {
                final boolean mediaFileUploadComplete = this.isMediaFileUploadComplete();
                if (mediaFileUploadComplete) {
                    final String mediaFileNo = this.getMediaFileNo();
                    if (!"0000".equals(mediaFileNo)) {
                        resultInternal.setData(mediaFileNo);
                        SyncResultSupport.getLabelResultCountDown(label).countDown();
                    }
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
    public StateEnum isOnline() {
        return StateEnum.valueBy(zhongHeResponse.getContent().substring(0, 4));
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
        return ZhongHeUtils.changeOrder(zhongHeResponse.getContent().substring(16, 20), 2);
    }
}
