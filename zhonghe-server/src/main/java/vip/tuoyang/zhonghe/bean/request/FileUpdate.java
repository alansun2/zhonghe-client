package vip.tuoyang.zhonghe.bean.request;

import lombok.Getter;
import lombok.Setter;
import vip.tuoyang.base.exception.BizException;
import vip.tuoyang.base.util.StringUtils;

/**
 * @author AlanSun
 * @date 2021/9/27 23:40
 **/
@Getter
@Setter
public class FileUpdate {
    /**
     * 自己版本号
     */
    private String fileName;
    /**
     * 更新自己的 url
     */
    private String fileUrl;
}