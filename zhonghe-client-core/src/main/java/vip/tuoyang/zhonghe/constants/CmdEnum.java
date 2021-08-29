package vip.tuoyang.zhonghe.constants;

import lombok.Getter;
import vip.tuoyang.base.exception.BizException;

/**
 * @author AlanSun
 * @date 2021/8/27 17:08
 */
@Getter
public enum CmdEnum {
    /**
     * test
     */
    TEST("00"),
    INIT("01"),
    INQUIRY_INIT("11"),
    CLOSE("02"),
    RESET("03"),
    STATE("04"),
    DOWNLOAD_DATA("05"),
    REQUEST_DEVICE_TASK("06"),
    REQUEST_EDITABLE_TASK("07"),
    ABORT_DEVICE_TASK("08"),
    ABORT_ALL_TASK("09"),
    ABORT_TASK_BY_SUB_ID("0A"),
    SET_TASK_VOLUME("0B"),
    SET_PLAYER_VOLUME("0C"),
    PRO_TIMING_TASK("0D"),
    DELETE_MEDIA_FILE("0E"),
    UPLOAD_MEDIA_FILE("0F"),
    GET_TASK_RUN_INFO("16"),
    START_CAPTURING("C0"),
    STOP_CAPTURING("C1"),
    AUDIO_DATA1("AD"),
    AUDIO_DATA2("An"),
    SEND_STATE("85"),
    SEND_DATA("8D"),
    SEND_NEXT_DATA("80"),
    SET_CAPTURER("30"),
    GET_SOUND_CARD_INFO("31"),
    GET_CAPTURING_INFO("32"),
    SEND_CAPTURING_STA("35"),
    ;

    private final String value;

    CmdEnum(String value) {
        this.value = value;
    }

    public static CmdEnum valueBy(String cmdStr) {
        switch (cmdStr) {
            case "00":
                return TEST;
            case "01":
                return INIT;
            case "11":
                return INQUIRY_INIT;
            case "02":
                return CLOSE;
            case "03":
                return RESET;
            case "04":
                return STATE;
            case "05":
                return DOWNLOAD_DATA;
            case "06":
                return REQUEST_DEVICE_TASK;
            case "07":
                return REQUEST_EDITABLE_TASK;
            case "08":
                return ABORT_DEVICE_TASK;
            case "09":
                return ABORT_ALL_TASK;
            case "0A":
                return ABORT_TASK_BY_SUB_ID;
            case "0B":
                return SET_TASK_VOLUME;
            case "0C":
                return SET_PLAYER_VOLUME;
            case "0D":
                return PRO_TIMING_TASK;
            case "0E":
                return DELETE_MEDIA_FILE;
            case "0F":
                return UPLOAD_MEDIA_FILE;
            case "16":
                return GET_TASK_RUN_INFO;
            case "C0":
                return START_CAPTURING;
            case "C1":
                return STOP_CAPTURING;
            case "AD":
                return AUDIO_DATA1;
            case "An":
                return AUDIO_DATA2;
            case "85":
                return SEND_STATE;
            case "8D":
                return SEND_DATA;
            case "80":
                return SEND_NEXT_DATA;
            case "30":
                return SET_CAPTURER;
            case "31":
                return GET_SOUND_CARD_INFO;
            case "32":
                return GET_CAPTURING_INFO;
            case "35":
                return SEND_CAPTURING_STA;
            default:
                throw new BizException("命令不存在");
        }
    }
}
