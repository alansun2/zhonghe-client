package vip.tuoyang.zhonghe.bean;

import lombok.Getter;
import lombok.Setter;

/**
 * @author AlanSun
 * @date 2021/8/28 17:04
 */
@Getter
@Setter
public class ZhongHeResult<T> {

    private boolean isSuccess = true;

    private String errorMsg;

    private String originalData;

    private T data;
}
