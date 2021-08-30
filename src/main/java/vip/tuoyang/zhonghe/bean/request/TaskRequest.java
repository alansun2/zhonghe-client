package vip.tuoyang.zhonghe.bean.request;

import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Range;
import vip.tuoyang.base.constants.SeparatorConstants;
import vip.tuoyang.base.exception.BizException;
import vip.tuoyang.base.util.AssertUtils;
import vip.tuoyang.zhonghe.utils.ConvertCode;
import vip.tuoyang.zhonghe.utils.ServiceUtils;

import javax.validation.constraints.NotNull;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author AlanSun
 * @date 2021/8/26 14:30
 */
public class TaskRequest {
    /**
     * 任务名称
     */
    @Setter
    private String taskName;
    /**
     * 任务类型: 0:文件播放;1:采播设备
     */
    @Setter
    @NotNull(message = "任务类型不能为空")
    private Byte taskType;
    /**
     * 开始时间
     */
    @Setter
    private LocalDateTime startTime;
    /**
     * 结束时间
     */
    @Setter
    private LocalDateTime endTime;
    /**
     * 星期选项 1,2,3,4,5,6,7
     * <p>
     * 逗号隔开
     */
    @Setter
    private String weekOption;
    /**
     * Bit7:3 为循环次数，仅在循环模式下有效，可指定循
     * 环次数为 1～31，0 为不限制循环次数。
     */
    @Setter
    private Byte count = 0;
    /**
     * 播放模式
     * 0：单曲播放 1：单曲循环 2：顺序播放; 3：列表循环 4：随机播放
     */
    @Setter
    private Byte playMode;
    /**
     * 播放列表
     */
    @Setter
    private List<String> playContentIdList;
    /**
     * 播放目标列表
     */
    @Setter
    private List<String> playObjectIdList;
    /**
     * 是否提前打开功放: 0:否:1:是
     */
    @Setter
    private Byte isOpenPowerAmplifierInAdvance = 0;
    /**
     * 提前打开功放的时间值，取值范围 0～16，单位秒。
     * 该字节是否有效受第 3 字节 Bit6 控制，无效时填 0。
     */
    @Setter
    @Range(min = 0, max = 16, message = "提前打开功放的时间范围为0~16")
    private Integer isOpenPowerAmplifierInAdvanceTime = 0;
    /**
     * 可编辑任务：
     * 0 -> 00 保留勿用
     * 1 -> 01 绝对时间 Calendar
     * 2 -> 10 即时执行 Instant（开始时间无效）
     * 3 -> 11相对时间 Relative
     * 定时任务：
     * 0 -> 00 星期循环 WeekLoop
     * 1 -> 01 绝对时间 Calendar
     * 2/3 -> 10/11 保留勿用
     */
    @Range(min = 0, max = 3, message = "timeMode 值范围为0~3")
    @Setter
    private Byte timeMode;
    /**
     * 可编辑任务：
     * 0 -> 00 指定时刻 Time（结束时间无效）
     * 1 -> 01 指定时段 Period
     * 2 -> 10 保留勿用
     * 3 -> 11 持续时间 Duration
     * 定时任务：
     * 0 -> 00 指定时刻 Time（结束时间无效）
     * 1 -> 01 指定时段 Period
     * 2/3 -> 10/11 保留勿用
     */
    @Setter
    @Range(min = 0, max = 3, message = "timeType 值范围为0~3")
    private Byte timeType;
    /**
     * 任务的音量值，取值范围 0～127，单位-dB。
     * 0dB 是最大音量，值 127 对应-127dB 为最小音量。
     */
    @Setter
    @Range(min = 0, max = 127, message = "任务的音量值范围为0~127")
    private Integer volume = 0;

    public void valid() {
        if (taskType == 0) {
            if ((playMode == 1 || playMode == 3) && (count < 0 || count > 31)) {
                throw new BizException("循环次数为1~31");
            }
        }

        if (isOpenPowerAmplifierInAdvance == 1) {
            AssertUtils.isTrue(isOpenPowerAmplifierInAdvanceTime >= 0 && isOpenPowerAmplifierInAdvanceTime <= 16, "提前开始的时间范围为0~16");
        }
    }

    /**
     * 获取任务类型
     */
    public String getTaskType() {
        return StringUtils.leftPad(taskType.toString(), 2, '0');
    }

    public String getTime() {
        return StringUtils.leftPad(Integer.toHexString(Integer.parseInt("0" + isOpenPowerAmplifierInAdvance.toString() +
                StringUtils.leftPad(Integer.toBinaryString(timeMode), 2, '0') +
                "00" +
                StringUtils.leftPad(Integer.toBinaryString(timeType), 2, '0'), 2)), 2, '0');
    }

    public String getTaskName() {
        final String s = ConvertCode.bytes2HexString(ServiceUtils.toGbkBytes(taskName));
        final String len = ConvertCode.intToHexString(s.length() / 2, 1);
        return StringUtils.rightPad(len + s, 64, '0');
    }

    public String getStartTime() {
        if (startTime == null) {
            return "000000000000";
        }
        return ServiceUtils.localDateTimeToHex(startTime);
    }

    public String getEndTime() {
        if (endTime == null) {
            return "000000000000";
        }
        return ServiceUtils.localDateTimeToHex(endTime);
    }

    public String getPlayWay() {
        return ConvertCode.binStr2HexString(ConvertCode.int2BinString(count, 5) + ConvertCode.int2BinString(playMode, 3), 1);
    }


    public String getWeek() {
        if (StringUtils.isEmpty(weekOption)) {
            return "00";
        }

        StringBuilder sb = new StringBuilder();
        final Set<Integer> weekIntSet = Arrays.stream(weekOption.split(SeparatorConstants.COMMA)).map(Integer::parseInt).collect(Collectors.toSet());
        final DayOfWeek[] values = DayOfWeek.values();
        for (int i = values.length - 1; i >= 0; i--) {
            DayOfWeek value = values[i];
            if (weekIntSet.contains(value.getValue())) {
                sb.append("1");
            } else {
                sb.append("0");
            }
        }

        return ConvertCode.binStr2HexString(sb.append("0").toString(), 1);
    }

    public String getVolume() {
        return ConvertCode.intToHexString(volume, 1);
    }

    public String getOpenInAdvanceTime() {
        return ConvertCode.intToHexString(isOpenPowerAmplifierInAdvanceTime, 1);
    }

    public String getPlayContentLen() {
        if (taskType == 1) {
            return "02";
        }

        if (CollectionUtils.isEmpty(playContentIdList)) {
            return "00";
        }

        if ((playContentIdList.size() & 1) == 1) {
            return ConvertCode.intToHexString(playContentIdList.size() + 1, 1);
        } else {
            return ConvertCode.intToHexString(playContentIdList.size(), 1);
        }
    }

    public String getPlayObjectLen() {
        if (CollectionUtils.isEmpty(playObjectIdList)) {
            return "00";
        }

        return ConvertCode.intToHexString(playObjectIdList.size(), 1);
    }

    public String getPlayContent() {
        if (CollectionUtils.isEmpty(playContentIdList)) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (String playContentId : playContentIdList) {
            sb.append(ServiceUtils.changeOrder(playContentId, 2));
        }

        if ((playContentIdList.size() & 1) == 1) {
            sb.append("0000");
        }
        return sb.toString();
    }

    public String getPlayObject() {
        if (CollectionUtils.isEmpty(playObjectIdList)) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (String playObjectId : playObjectIdList) {
            sb.append(ServiceUtils.changeOrder(playObjectId, 2));
        }
        return sb.toString();
    }
}
