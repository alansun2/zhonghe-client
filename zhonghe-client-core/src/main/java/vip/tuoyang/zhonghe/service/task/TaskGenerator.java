package vip.tuoyang.zhonghe.service.task;

import vip.tuoyang.zhonghe.bean.request.TaskRequest;

/**
 * @author AlanSun
 * @date 2021/8/26 15:38
 */
public interface TaskGenerator {
    /**
     * 生成任务
     *
     * @param id      id
     * @param request {@link TaskRequest}
     * @return 16进制字符串
     */
    String generator(String id, TaskRequest request);
}
