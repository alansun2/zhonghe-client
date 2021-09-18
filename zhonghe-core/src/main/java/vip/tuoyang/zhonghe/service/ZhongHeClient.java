package vip.tuoyang.zhonghe.service;

import vip.tuoyang.zhonghe.bean.ZhongHeResult;
import vip.tuoyang.zhonghe.bean.request.TaskRequest;
import vip.tuoyang.zhonghe.bean.response.GroupDataResponse;
import vip.tuoyang.zhonghe.bean.response.MediaFileDataResponse;
import vip.tuoyang.zhonghe.bean.response.StateResponse;
import vip.tuoyang.zhonghe.bean.response.TerminalDataResponse;
import vip.tuoyang.zhonghe.config.ZhongHeConfig;

import java.io.InputStream;
import java.util.List;

/**
 * @author AlanSun
 * @date 2021/8/29 11:13
 */
public interface ZhongHeClient {
    SendClient getSendClient();

    ZhongHeConfig getZhongHeConfig();

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
    ZhongHeResult<?> initMiddleWare(boolean needClose);

    /**
     * 关闭
     *
     * @return {@link ZhongHeResult}
     */
    ZhongHeResult<?> close(boolean isCloseChannel);

    /**
     * 获取中间件状态
     *
     * @return 1
     */
    ZhongHeResult<StateResponse> state();

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
    ZhongHeResult<String> editTimingTask(String id, TaskRequest request);

    /**
     * 删除定时任务
     *
     * @param id id
     * @return {@link ZhongHeResult}
     */
    ZhongHeResult<?> deleteTimingTask(String id, TaskRequest request);

    /**
     * 添加可编辑任务
     *
     * @param request {@link TaskRequest}
     * @return {@link ZhongHeResult}
     */
    ZhongHeResult<String> addEditableTask(TaskRequest request);

    /**
     * 终止指定id的任务
     *
     * @param id id
     * @return {@link ZhongHeResult}
     */
    ZhongHeResult<?> abortTaskBySubId(String id);

    //-----------------------------upload-------------------------------------------------------------------------------

    /**
     * 上传文件
     *
     * @param inputStream inputStream
     * @param fileName    fileName
     * @return {@link ZhongHeResult}
     */
    ZhongHeResult<String> uploadMediaFile(InputStream inputStream, String fileName);

    /**
     * 删除媒体文件
     *
     * @param fileId fileId
     * @return {@link ZhongHeResult}
     */
    ZhongHeResult<?> deleteMediaFile(String fileId, String fileName);

    //------------------------------download data-----------------------------------------------------------------------

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
