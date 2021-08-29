package vip.tuoyang.zhonghe.service;

import vip.tuoyang.zhonghe.bean.ZhongHeResult;
import vip.tuoyang.zhonghe.bean.request.TaskRequest;
import vip.tuoyang.zhonghe.bean.response.GroupDataResponse;
import vip.tuoyang.zhonghe.bean.response.MediaFileDataResponse;
import vip.tuoyang.zhonghe.bean.response.TerminalDataResponse;

import java.util.List;

/**
 * @author AlanSun
 * @date 2021/8/29 11:13
 */
public interface ZhongHeClient {
    /**
     * test
     *
     * @return {@link ZhongHeResult}
     */
    ZhongHeResult<?> test();

    /**
     * 初始化中间件
     *
     * @return {@link ZhongHeResult}
     */
    ZhongHeResult<?> initMiddleWare();

    /**
     * 关闭
     *
     * @return {@link ZhongHeResult}
     */
    ZhongHeResult<?> close();

    //------------------------------------------task--------------------------------------------------------------------

    /**
     * 新增定时任务
     *
     * @param request {@link TaskRequest}
     * @return {@link ZhongHeResult}
     */
    ZhongHeResult<String> addTimingTask(TaskRequest request);

    /**
     * 编辑任务
     *
     * @param id      id
     * @param request {@link TaskRequest}
     * @return {@link ZhongHeResult}
     */
    ZhongHeResult<?> editTimingTask(String id, TaskRequest request);

    /**
     * 删除定时任务
     *
     * @param id id
     * @return {@link ZhongHeResult}
     */
    ZhongHeResult<?> deleteTimingTask(String id, TaskRequest request);

    /**
     * 终止指定id的任务
     *
     * @param id id
     * @return {@link ZhongHeResult}
     */
    ZhongHeResult<?> abortTaskBySubId(String id);

    //------------------------------download data----------------------------------------------------------------------------

    /**
     * 获取播放终端列表
     */
    ZhongHeResult<List<TerminalDataResponse>> getPlayersByNos();

    /**
     * 获取终端分组
     */
    ZhongHeResult<List<GroupDataResponse>> getTerminalGroups();

    /**
     * 获取媒体文件列表
     */
    ZhongHeResult<List<MediaFileDataResponse>> getMediaFiles();
}
