package vip.tuoyang.zhonghe.bean;

import lombok.Getter;
import lombok.Setter;

/**
 * @author AlanSun
 * @date 2021/8/28 15:03
 */
@Getter
@Setter
public class ZhongHeDownloadResult {

    private boolean isSuccess = true;

    private String errorMsg;

    private Object data;
}
