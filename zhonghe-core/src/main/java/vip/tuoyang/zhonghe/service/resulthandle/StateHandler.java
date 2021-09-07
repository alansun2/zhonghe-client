package vip.tuoyang.zhonghe.service.resulthandle;

import vip.tuoyang.zhonghe.bean.ResultInternal;
import vip.tuoyang.zhonghe.bean.response.StateResponse;
import vip.tuoyang.zhonghe.bean.response.ZhongHeResponse;
import vip.tuoyang.zhonghe.support.SendStateHandler;

/**
 * @author AlanSun
 * @date 2021/9/1 14:27
 */
public class StateHandler implements ResultHandler {
    private StateHandler() {
    }

    private static final StateHandler HANDLER = new StateHandler();

    public static StateHandler getInstance() {
        return HANDLER;
    }

    @Override
    public void handler(ZhongHeResponse zhongHeResponse, ResultInternal resultInternal) {
        SendStateHandler sendStateHandler = new SendStateHandler(zhongHeResponse);
        StateResponse stateResponse = new StateResponse();
        stateResponse.setState(sendStateHandler.isOnline());
        resultInternal.setData(stateResponse);
    }
}
