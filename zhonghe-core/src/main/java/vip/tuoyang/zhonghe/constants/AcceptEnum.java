package vip.tuoyang.zhonghe.constants;

import lombok.Getter;

/**
 * @author AlanSun
 * @date 2021/8/28 14:12
 **/
@Getter
public enum AcceptEnum {
    /**
     * 成功
     */
    SUCCESS("00", null),
    /**
     * 为 1 时表明中间件未上线
     */
    NOT_ONLINE("01", "服务已掉线"),
    /**
     * 为 1 时表明中间件未运行
     */
    NOT_RUNNING("02", "中间件未运行"),
    /**
     * 为 1 时表明中间件和 NAS 服务器之间通信正忙，上一
     * 操作未完成
     */
    BUSY_WITH_NAS("04", "中间件忙"),
    /**
     * 为 1 时表明命令附加参数长度不对，或请求下载数据
     * 时 Para 的值超出范围。
     */
    PARAM_ERROR("07", "参数错误"),
    /**
     * 未知
     */
    UNKNOWN("??", "未知异常"),
    ;

    private final String value;
    private final String errorMsg;

    AcceptEnum(String value, String errorMsg) {
        this.value = value;
        this.errorMsg = errorMsg;
    }

    public static AcceptEnum valueBy(String accept) {
        switch (accept) {
            case "00":
                return SUCCESS;
            case "01":
                return NOT_ONLINE;
            case "02":
                return NOT_RUNNING;
            case "04":
                return BUSY_WITH_NAS;
            case "07":
                return PARAM_ERROR;
            default:
                return UNKNOWN;
        }
    }
}
