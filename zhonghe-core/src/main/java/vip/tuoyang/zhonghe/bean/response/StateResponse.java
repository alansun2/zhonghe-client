package vip.tuoyang.zhonghe.bean.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import vip.tuoyang.zhonghe.constants.StateEnum;

/**
 * @author AlanSun
 * @date 2021/9/1 16:28
 */
@ToString
@Getter
@Setter
public class StateResponse {

    private StateEnum state;
    /**
     * 是否正忙
     */
    private boolean isBusy;
    /**
     * isBusy = true
     * <p>
     * 0: 传输中；1：已完成
     */
    private byte transferStatus;
    /**
     * 命令执行完服务器返回的附加参数，其意义由最后执行的命令确定。
     * 除特别说明外可忽视此字节。
     */
    private Object returnObj;
    /**
     * 上传或下载文件时的进度值，百分比，值为 n 表示进度为 n%。
     * 仅在传输过程中有意义，传输未启动或结束后该值失效。
     */
    private Integer progress;
    /**
     * 文件 id
     */
    private String fileId;
}
