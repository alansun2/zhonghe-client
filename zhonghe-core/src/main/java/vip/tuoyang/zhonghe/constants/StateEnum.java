package vip.tuoyang.zhonghe.constants;

import lombok.Getter;

/**
 * @author AlanSun
 * @date 2021/9/7 13:54
 */
@Getter
public enum StateEnum {
    /**
     * 在线并且运行中
     */
    ONLINE_RUNNING("01AA", ""),
    /**
     * 服务挂了，中间件运行中
     */
    OFFLINE_RUNNING("00AA", "服务未启动"),
    /**
     * 中间件未初始化
     */
    OFFLINE_DOWN("0000", ""),

    UNKNOWN("", "未知异常");

    private final String value;

    private final String desc;

    StateEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static StateEnum valueBy(String stateStr) {
        switch (stateStr) {
            case "0000":
                return OFFLINE_DOWN;
            case "00AA":
                return OFFLINE_RUNNING;
            case "01AA":
                return ONLINE_RUNNING;
            default:
                return UNKNOWN;
        }
    }
}
