package vip.tuoyang.zhonghe.bean;

import lombok.Getter;
import lombok.Setter;
import vip.tuoyang.zhonghe.bean.response.ZhongHeResponse;

/**
 * @author AlanSun
 * @date 2021/8/28 15:03
 */
@Getter
@Setter
public class ResultInternal {

    private boolean isSuccess = true;

    private String errorMsg;

    private String originalData;

    private Object data;

    private ZhongHeResponse zhongHeResponse;

    public <T> ZhongHeResult<T> toZhongHeResult() {
        ZhongHeResult<T> zhongHeResult = new ZhongHeResult<>();
        zhongHeResult.setSuccess(this.isSuccess);
        zhongHeResult.setErrorMsg(this.errorMsg);
        zhongHeResult.setData((T) this.data);
        return zhongHeResult;
    }
}
