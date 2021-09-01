package vip.tuoyang.zhonghe.service.resulthandle;

import vip.tuoyang.zhonghe.bean.ResultInternal;
import vip.tuoyang.zhonghe.bean.response.ZhongHeResponse;

/**
 * @author AlanSun
 * @date 2021/9/1 14:27
 */
public class EditableTaskHandler implements ResultHandler {
    private EditableTaskHandler() {
    }

    private static final EditableTaskHandler HANDLER = new EditableTaskHandler();

    public static EditableTaskHandler getInstance() {
        return HANDLER;
    }

    @Override
    public void handler(ZhongHeResponse zhongHeResponse, ResultInternal resultInternal) {
        resultInternal.setData(zhongHeResponse.getContent());
    }
}
