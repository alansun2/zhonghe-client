package vip.tuoyang.zhonghe.bean.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author AlanSun
 * @date 2021/9/4 11:54
 */
@ToString
@Getter
@Setter
public class IpChangeRequest {
    /**
     * 变化后的 ip
     */
    private String ip;
    /**
     * 标签，用于用户查找对应的服务
     */
    private String label;
}
