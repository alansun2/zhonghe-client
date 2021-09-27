package vip.tuoyang.zhonghe.bean.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author AlanSun
 * @date 2021/9/27 10:21
 */
@Getter
@Setter
public class SoftUpdateResponse {

    private Integer successCount;

    private List<String> fileResult;
}
