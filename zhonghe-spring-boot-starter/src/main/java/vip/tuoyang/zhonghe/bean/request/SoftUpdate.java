package vip.tuoyang.zhonghe.bean.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author AlanSun
 * @date 2021/9/27 10:55
 */
@Getter
@Setter
public class SoftUpdate<T> {

    private List<String> labels;

    private T softUpdateRequest;
}
