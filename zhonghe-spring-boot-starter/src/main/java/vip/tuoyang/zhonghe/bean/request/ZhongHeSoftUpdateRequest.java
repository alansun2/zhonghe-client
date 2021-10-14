package vip.tuoyang.zhonghe.bean.request;

import lombok.Getter;
import lombok.Setter;
import vip.tuoyang.base.exception.BizException;
import vip.tuoyang.base.util.AssertUtils;
import vip.tuoyang.base.util.StringUtils;

/**
 * @author AlanSun
 * @date 2021/9/27 9:17
 */
@Getter
@Setter
public class ZhongHeSoftUpdateRequest {
    /**
     * 版本号
     */
    private String version;
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
        if (StringUtils.isEmpty(nasUrl) && StringUtils.isEmpty(manageUrl) && StringUtils.isEmpty(middlewareUrl)) {
            throw new BizException("三个地址不能都为空");
        }
    }
}
