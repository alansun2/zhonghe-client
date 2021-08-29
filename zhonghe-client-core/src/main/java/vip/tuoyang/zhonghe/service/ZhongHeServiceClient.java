package vip.tuoyang.zhonghe.service;

import vip.tuoyang.zhonghe.bean.ResultInternal;
import vip.tuoyang.zhonghe.bean.ZhongHeDownloadResult;
import vip.tuoyang.zhonghe.bean.ZhongHeResult;
import vip.tuoyang.zhonghe.bean.request.TaskRequest;
import vip.tuoyang.zhonghe.bean.response.MediaFileDataResponse;
import vip.tuoyang.zhonghe.constants.CmdEnum;
import vip.tuoyang.zhonghe.service.task.TimingFileTask;
import vip.tuoyang.zhonghe.support.SyncResultSupport;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author AlanSun
 * @date 2021/8/24 16:42
 */
public class ZhongHeServiceClient {

    public ZhongHeServiceClient() {
    }

    /**
     * 新增定时任务
     *
     * @param request {@link TaskRequest}
     */
    public void addTimingTask(TaskRequest request) {
        final String generator = TimingFileTask.getInstance().generator("FE", request);

        SendClient.getSingleton().send(CmdEnum.PRO_TIMING_TASK, "01", generator);
    }

    /**
     * 编辑任务
     *
     * @param id      id
     * @param request {@link TaskRequest}
     */
    public void editTimingTask(String id, TaskRequest request) {
        final String generator = TimingFileTask.getInstance().generator(id, request);

        SendClient.getSingleton().send(CmdEnum.PRO_TIMING_TASK, "02", generator);
    }

    /**
     * 删除定时任务
     *
     * @param id id
     */
    public void deleteTimingTask(String id) {

    }

    //------------------------------download data----------------------------------------------------------------------------

    /**
     * 获取媒体文件列表
     */
    public ZhongHeResult<List<MediaFileDataResponse>> getMediaFiles() {
        ZhongHeResult<List<MediaFileDataResponse>> zhongHeResult = new ZhongHeResult<>();
        final ResultInternal resultInternal = SendClient.getSingleton().send(CmdEnum.DOWNLOAD_DATA, "04", null);
        if (resultInternal.isSuccess()) {
            try {
                SyncResultSupport.downloadResultDataCountDown.await(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            final ZhongHeDownloadResult zhongHeDownloadResult = SyncResultSupport.downloadParaResultMap.get("04");
            if (zhongHeDownloadResult.isSuccess()) {
                final List<MediaFileDataResponse> data = (List<MediaFileDataResponse>) zhongHeDownloadResult.getData();
                zhongHeResult.setData(data);
            } else {
                zhongHeResult.setSuccess(false);
                zhongHeResult.setErrorMsg(zhongHeDownloadResult.getErrorMsg());
            }
        } else {
            zhongHeResult.setSuccess(false);
            zhongHeResult.setErrorMsg(resultInternal.getErrorMsg());
        }
        return zhongHeResult;
    }

    /**
     * 获取终端分组
     */
    public void getTerminalGroups() {
        SendClient.getSingleton().send(CmdEnum.DOWNLOAD_DATA, "03", null);
    }
}
