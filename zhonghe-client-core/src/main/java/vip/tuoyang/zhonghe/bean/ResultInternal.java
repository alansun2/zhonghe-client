package vip.tuoyang.zhonghe.bean;

import lombok.Getter;
import lombok.Setter;

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
}
