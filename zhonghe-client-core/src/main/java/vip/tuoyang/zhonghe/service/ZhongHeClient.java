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
     */
    void test();

    /**
     * 初始化中间件
     */
    void initMiddleWare();

    /**
     * 关闭
     */
    void close();

    //------------------------------------------task--------------------------------------------------------------------

    /**
     * 新增定时任务
     *
     * @param request {@link TaskRequest}
     */
    void addTimingTask(TaskRequest request);

    /**
     * 编辑任务
     *
     * @param id      id
     * @param request {@link TaskRequest}
     */
    void editTimingTask(String id, TaskRequest request);

    /**
     * 删除定时任务
     *
     * @param id id
     */
    void deleteTimingTask(String id);

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
