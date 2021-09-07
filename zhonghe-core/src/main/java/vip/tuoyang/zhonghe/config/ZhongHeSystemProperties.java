package vip.tuoyang.zhonghe.config;

import lombok.Getter;
import lombok.Setter;

/**
 * @author AlanSun
 * @date 2021/9/3 12:46
 */
@Getter
@Setter
public class ZhongHeSystemProperties {
    /**
     * 请求密码
     */
    public static String secret;
    /**
     * 超时时间,单位秒
     */
    public static int timeout = 5;
}
