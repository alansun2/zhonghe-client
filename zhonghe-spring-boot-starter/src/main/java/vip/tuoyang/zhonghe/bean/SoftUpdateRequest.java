package vip.tuoyang.zhonghe.bean;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import vip.tuoyang.base.exception.BizException;
import vip.tuoyang.base.util.AssertUtils;

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

    public void valid() {
        AssertUtils.notBlank(version, "version null error");
        if (StringUtils.isAllEmpty(nasUrl, manageUrl, middlewareUrl)) {
            throw new BizException("三个地址不能都为空");
        }
    }
}
