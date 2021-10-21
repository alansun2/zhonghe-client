package vip.tuoyang.zhonghe.bean.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @author AlanSun
 * @date 2021/10/21 13:52
 */
@Getter
@Setter
public class CommandRequest {
    /**
     * labels
     */
    @NotEmpty(message = "labels 不能为空")
    private List<String> labels;
    /**
     * 指令
     */
    @NotBlank(message = "指令不能为空")
    private String command;
}
