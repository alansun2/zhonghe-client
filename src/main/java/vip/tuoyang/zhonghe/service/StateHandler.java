package vip.tuoyang.zhonghe.service;

import vip.tuoyang.zhonghe.bean.ResultInternal;
import vip.tuoyang.zhonghe.bean.response.ZhongHeResponse;
import vip.tuoyang.zhonghe.constants.CmdEnum;
import vip.tuoyang.zhonghe.support.SyncResultSupport;

/**
 * @author AlanSun
 * @date 2021/8/29 16:09
 */
public class StateHandler {
    public static void fillData(ZhongHeResponse zhongHeResponse) {
        final ResultInternal resultInternal = SyncResultSupport.resultInternal;
        if (resultInternal != null) {
            final CmdEnum cmdEnum = resultInternal.getZhongHeResponse().getCmdEnum();
            if (cmdEnum == CmdEnum.PRO_TIMING_TASK || cmdEnum == CmdEnum.REQUEST_EDITABLE_TASK) {
                resultInternal.setData(zhongHeResponse.getContent().substring(8, 10));
                SyncResultSupport.cmdResultCountDownMap.get(cmdEnum).countDown();
            }
        }
    }
}
