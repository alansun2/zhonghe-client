package vip.tuoyang.zhonghe.service.resulthandle;

import vip.tuoyang.zhonghe.bean.response.ZhongHeResponse;
import vip.tuoyang.zhonghe.service.downloadhandler.DownloadHandlerContext;

/**
 * @author AlanSun
 * @date 2021/8/27 15:19
 */
public class DownloadReceiverHandler implements ResultHandler {

    @Override
    public void handler(ZhongHeResponse zhongHeResponse) {
        final int sn = zhongHeResponse.getSn();
        long end = 0b10000000000000000L;
        if ((sn & end) == end) {
            // 结束
        }

        final DownloadHandlerContext downloadHandlerContext = DownloadHandlerContext.create(zhongHeResponse.getPara());
        downloadHandlerContext.handler(zhongHeResponse);
    }
}
;