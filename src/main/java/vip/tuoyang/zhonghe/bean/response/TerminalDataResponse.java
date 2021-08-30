package vip.tuoyang.zhonghe.bean.response;

import lombok.Getter;
import lombok.Setter;

/**
 * @author AlanSun
 * @date 2021/8/29 10:20
 */
@Getter
@Setter
public class TerminalDataResponse {
    /**
     * 播放终端设备 ID，
     */
    private String terminalNo;
    /**
     * 终端状态：0：正常在线 1：正在注册中 2：故障 3：离线
     */
    private Byte terminalStatus;
    /**
     * 名称
     */
    private String terminalName;
}
