package vip.tuoyang.zhonghe.support;

import vip.tuoyang.zhonghe.bean.ResultInternal;
import vip.tuoyang.zhonghe.bean.response.ZhongHeResponse;
import vip.tuoyang.zhonghe.constants.CmdEnum;
import vip.tuoyang.zhonghe.support.SyncResultSupport;

/**
 * @author AlanSun
 * @date 2021/8/29 16:09
 */
public class SendStateHandler {
    public static void handle(ZhongHeResponse zhongHeResponse, String label) {
        final ResultInternal resultInternal = SyncResultSupport.labelResultInternal.get(label);
        if (resultInternal != null) {
            final CmdEnum cmdEnum = resultInternal.getZhongHeResponse().getCmdEnum();
            if (cmdEnum == CmdEnum.PRO_TIMING_TASK) {
                resultInternal.setData(zhongHeResponse.getContent().substring(8, 10));
                SyncResultSupport.labelResultCountDownMap.get(label).countDown();
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
}
