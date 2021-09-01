package vip.tuoyang.zhonghe.service.resulthandle;

import vip.tuoyang.zhonghe.bean.ResultInternal;
import vip.tuoyang.zhonghe.bean.response.ZhongHeResponse;
import vip.tuoyang.zhonghe.service.downloadhandler.DownloadHandlerContext;

/**
 * @author AlanSun
 * @date 2021/8/27 15:19
 */
public class DownloadReceiverHandler implements ResultHandler {
    private DownloadReceiverHandler() {
    }

    private static final DownloadReceiverHandler HANDLER = new DownloadReceiverHandler();

    public static DownloadReceiverHandler getInstance() {
        return HANDLER;
    }

    @Override
    public void handler(ZhongHeResponse zhongHeResponse, ResultInternal resultInternal) {
        final DownloadHandlerContext downloadHandlerContext = DownloadHandlerContext.create(zhongHeResponse.getPara());
        downloadHandlerContext.handler(zhongHeResponse);
    }
}