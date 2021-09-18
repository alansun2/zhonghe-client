package vip.tuoyang.zhonghe.service.task;

import vip.tuoyang.zhonghe.bean.request.TaskRequest;

/**
 * @author AlanSun
 * @date 2021/8/26 15:40
 */
public class TimingFileTask implements TaskGenerator {

    private TimingFileTask() {
    }

    private static final TimingFileTask TIMING_FILE_TASK = new TimingFileTask();

    public static TimingFileTask getInstance() {
        return TIMING_FILE_TASK;
    }

    /**
     * 生成任务
     *
     * @param request {@link TaskRequest}
     * @return 16进制字符串
     */
    @Override
    public String generator(String id, TaskRequest request) {

        return "00000000" +
                id + request.getTaskTypeHex() + "00" +
                // timeType timeMode
                request.getTime() +
                request.getStartTimeHex() +
                request.getEndTimeHex() +
                request.getWeekHex() +
                request.getPlayWayHex() +
                request.getPlayContentLen() +
                request.getPlayObjectLen() +
                request.getVolumeHex() +
                request.getOpenInAdvanceTimeHex() +
                // 保留
                "0000" +
                request.getTaskNameHex() +
                request.getPlayContent() +
                request.getPlayObject();
    }
}
