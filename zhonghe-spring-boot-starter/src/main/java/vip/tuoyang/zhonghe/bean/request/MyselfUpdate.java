package vip.tuoyang.zhonghe.bean.request;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import vip.tuoyang.base.exception.BizException;
import vip.tuoyang.base.util.AssertUtils;

/**
 * @author AlanSun
 * @date 2021/9/27 23:40
 **/
@Getter
@Setter
public class MyselfUpdate {
    /**
     * 自己版本号
     */
    private String version;
    /**
     * 更新自己的 url
     */
    private String myselfUrl;

    public void valid() {
        AssertUtils.notBlank(version, "version null error");
        if (StringUtils.isAllEmpty(myselfUrl)) {
            throw new BizException("三个地址不能都为空");
        }
    }
}