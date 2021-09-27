package vip.tuoyang.zhonghe.bean.request;

import lombok.Getter;
import lombok.Setter;

/**
 * @author AlanSun
 * @date 2021/9/27 9:17
 */
@Getter
@Setter
public class SoftUpdateRequest {
    /**
     * 版本号
     */
    private String version;
    /**
     * 下载地址
     */
    private String downloadUrl;
    /**
     * nas url
     */
    private String nasUrl;
    /**
     * manage url
     */
    private String manageUrl;
    /**
     * 中间件 url
     */
    private String middlewareUrl;
}
