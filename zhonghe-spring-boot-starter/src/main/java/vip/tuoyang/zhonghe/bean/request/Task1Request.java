package vip.tuoyang.zhonghe.bean.request;

import lombok.Getter;
import lombok.Setter;

/**
 * @author AlanSun
 * @date 2021/9/23 0:06
 */
@Getter
@Setter
public class Task1Request {

    private String taskNo;

    private TaskRequest taskRequest;

    public Task1Request(String taskNo, TaskRequest taskRequest) {
        this.taskNo = taskNo;
        this.taskRequest = taskRequest;
    }
}
