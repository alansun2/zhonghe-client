package vip.tuoyang.zhonghe.service.downloadhandler;

import lombok.extern.slf4j.Slf4j;
import vip.tuoyang.zhonghe.bean.ZhongHeDownloadResult;
import vip.tuoyang.zhonghe.bean.response.ZhongHeResponse;
import vip.tuoyang.zhonghe.constants.CmdEnum;
import vip.tuoyang.zhonghe.constants.DownloadTypeEnum;
import vip.tuoyang.zhonghe.service.SendClient;
import vip.tuoyang.zhonghe.support.SyncResultSupport;

/**
 * @author AlanSun
 * @date 2021/8/27 14:28
 */
@Slf4j
public class DownloadHandlerContext {
    private DownLoadResultHandler downLoadResultHandler;

    private DownloadHandlerContext(String para) {
        final DownloadTypeEnum downloadTypeEnum = DownloadTypeEnum.valueBy(para);
        switch (downloadTypeEnum) {
            case TERMINAL_PLAYER:
                downLoadResultHandler = PlayerDataDownloadHandler.getInstance();
                break;
            case TERMINAL_GROUP:
                downLoadResultHandler = GroupDataDownloadHandler.getInstance();
                break;
            case MEDIA_FILE:
                downLoadResultHandler = MediaFileDataDownloadHandler.getInstance();
                break;
            default:
                log.info("接收到忽略指令: [{}]", para);
        }
    }

    public static DownloadHandlerContext create(String para) {
        return new DownloadHandlerContext(para);
    }

    /**
     * 返回数据处理
     *
     * @param zhongHeResponse {@link ZhongHeResponse}
     */
    public void handler(ZhongHeResponse zhongHeResponse) {
        if (zhongHeResponse.getContent().length() == 0) {
            SyncResultSupport.downloadParaResultMap.put(zhongHeResponse.getPara(), new ZhongHeDownloadResult());
            SyncResultSupport.downloadResultDataCountDown.countDown();
        } else {
            downLoadResultHandler.handler(zhongHeResponse.getPara(), zhongHeResponse.getContent());
            if (zhongHeResponse.isLastSn()) {
                SyncResultSupport.downloadResultDataCountDown.countDown();
            } else {
                SendClient.getSingleton().sendAsync(CmdEnum.SEND_NEXT_DATA, "00", zhongHeResponse.getSn(), null);
            }
        }
    }
}
