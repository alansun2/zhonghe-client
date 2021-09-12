package vip.tuoyang.zhonghe.service.task;

import vip.tuoyang.zhonghe.bean.request.TaskRequest;

/**
 * @author AlanSun
 * @date 2021/8/26 15:40
 */
public class EditableTask implements TaskGenerator {

    private EditableTask() {
    }

    private static final EditableTask TIMING_FILE_TASK = new EditableTask();

    public static EditableTask getInstance() {
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

        return id + request.getTaskType() + "00" +
                // timeType timeMode
                request.getTime() +
                request.getStartTimeHex() +
                request.getEndTimeHex() +
                request.getWeek() +
                request.getPlayWay() +
                request.getPlayContentLen() +
                request.getPlayObjectLen() +
                request.getVolume() +
                request.getOpenInAdvanceTime() +
                // 保留
                "0000" +
                request.getTaskName() +
                request.getPlayContent() +
                request.getPlayObject();
    }
}
