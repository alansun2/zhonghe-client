package vip.tuoyang.zhonghe.bean.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author AlanSun
 * @date 2021/8/27 9:29
 */
@Getter
@Setter
public class GroupDataResponse {
    /**
     * 编号
     */
    private String no;
    /**
     * 文件名称
     */
    private String name;
    /**
     * 组内的终端id
     */
    private List<String> terminalNos;
}
