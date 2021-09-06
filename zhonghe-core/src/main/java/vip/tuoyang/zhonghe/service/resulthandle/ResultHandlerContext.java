package vip.tuoyang.zhonghe.service.resulthandle;

import lombok.extern.slf4j.Slf4j;
import vip.tuoyang.zhonghe.bean.ResultInternal;
import vip.tuoyang.zhonghe.bean.response.ZhongHeResponse;
import vip.tuoyang.zhonghe.constants.AcceptEnum;
import vip.tuoyang.zhonghe.constants.CmdEnum;

/**
 * @author AlanSun
 * @date 2021/8/27 16:10
 */
@Slf4j
public class ResultHandlerContext {

    private ResultHandler resultHandler;

    private ResultHandlerContext(CmdEnum cmdEnum) {
        switch (cmdEnum) {
            case SEND_DATA:
                resultHandler = DownloadReceiverHandler.getInstance();
                break;
            case REQUEST_EDITABLE_TASK:
                resultHandler = EditableTaskHandler.getInstance();
                break;
            case STATE:
                resultHandler = StateHandler.getInstance();
                break;
            default:
                log.info("忽略命令: [{}]", cmdEnum);
        }
    }

    public static ResultHandlerContext create(CmdEnum cmdEnum) {
        return new ResultHandlerContext(cmdEnum);
    }

    public ResultInternal handle(ZhongHeResponse zhongHeResponse) {
        ResultInternal resultInternal = new ResultInternal();
        resultInternal.setOriginalData(zhongHeResponse.getOriginalData());
        final AcceptEnum acceptEnum = zhongHeResponse.getAcceptEnum();
        if (!acceptEnum.equals(AcceptEnum.SUCCESS)) {
            resultInternal.setSuccess(false);
            resultInternal.setErrorMsg(acceptEnum.getErrorMsg());
            log.error(acceptEnum.getErrorMsg());
        }
        resultInternal.setSuccess(true);
        if (resultHandler != null) {
            resultHandler.handler(zhongHeResponse, resultInternal);
        }

        return resultInternal;
    }
}
