package vip.tuoyang.zhonghe;

import lombok.Getter;

/**
 * @author AlanSun
 * @date 2021/8/26 16:27
 **/
@Getter
public enum WeekEnum {
    /**
     * 星期一
     */
    MON((byte) 1),
    TUE((byte) 2),
    WEN((byte) 3),
    THE((byte) 4),
    FIR((byte) 5),
    STA((byte) 6),
    SUN((byte) 7),
    ;

    private final byte value;

    WeekEnum(byte value) {
        this.value = value;
    }
}
