package vip.tuoyang.zhonghe.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import vip.tuoyang.zhonghe.bean.ResultInternal;
import vip.tuoyang.zhonghe.bean.ZhongHeDownloadResult;
import vip.tuoyang.zhonghe.bean.ZhongHeResult;
import vip.tuoyang.zhonghe.bean.request.TaskRequest;
import vip.tuoyang.zhonghe.bean.response.GroupDataResponse;
import vip.tuoyang.zhonghe.bean.response.MediaFileDataResponse;
import vip.tuoyang.zhonghe.bean.response.TerminalDataResponse;
import vip.tuoyang.zhonghe.config.ZhongHeConfig;
import vip.tuoyang.zhonghe.constants.CmdEnum;
import vip.tuoyang.zhonghe.service.task.EditableTask;
import vip.tuoyang.zhonghe.service.task.TimingFileTask;
import vip.tuoyang.zhonghe.support.SyncResultSupport;
import vip.tuoyang.zhonghe.utils.ConvertCode;
import vip.tuoyang.zhonghe.utils.ServiceUtils;

import java.util.List;

/**
 * @author AlanSun
 * @date 2021/8/22 12:42
 **/
@Slf4j
public class ZhongHeClientImpl implements ZhongHeClient {
    private final ZhongHeConfig zhongHeConfig;

    public ZhongHeClientImpl(ZhongHeConfig zhongHeConfig) {
        zhongHeConfig.valid();
        this.zhongHeConfig = zhongHeConfig;
    }

    /**
     * test
     */
    @Override
    public ZhongHeResult<?> test() {
        return SendClient.getSingleton().send(CmdEnum.TEST, "00", null).toZhongHeResult();
    }

    /**
     * 初始化中间件
     */
    @Override
    public ZhongHeResult<?> initMiddleWare() {
        // 先关闭
        this.close();

        //// 构建初始化数据
        StringBuilder sb = new StringBuilder();
        // 16 17 deviceId
        sb.append(ServiceUtils.changeOrder(zhongHeConfig.getDeviceId(), 2));
        // 18 19 管理码
        sb.append(ServiceUtils.changeOrder(zhongHeConfig.getManagerCode(), 2));
        // 20 - 51描述
        sb.append(StringUtils.rightPad(ConvertCode.intToHexString(zhongHeConfig.getDeviceDes().length() * 2, 1) + ConvertCode.bytes2HexString(ServiceUtils.toGbkBytes(zhongHeConfig.getDeviceDes())).toUpperCase(), 64, '0'));
        // 52 53 54, 55 nas ip
        final String nasIp = zhongHeConfig.getNasIp();
        final String[] nasIpSplit = nasIp.split("\\.");
        for (String nasIpSegment : nasIpSplit) {
            sb.append(ConvertCode.intToHexString(Integer.parseInt(nasIpSegment), 1).toUpperCase());
        }
        // 56 57 连接端口
        sb.append(ConvertCode.int2HexLittle(zhongHeConfig.getNasConnectPort()));
        // 58 59 控制端口
        sb.append(ConvertCode.int2HexLittle(zhongHeConfig.getNasControlPort()));
        // 60 61 采播端口
        sb.append(ConvertCode.int2HexLittle(zhongHeConfig.getNasCapturePort()));
        // 62 63
        sb.append("0000");

        return SendClient.getSingleton().send(CmdEnum.INIT, "00", sb.toString()).toZhongHeResult();
    }

    /**
     * 关闭
     */
    @Override
    public ZhongHeResult<?> close() {
        return SendClient.getSingleton().send(CmdEnum.CLOSE, "00", null).toZhongHeResult();
    }

    //------------------------------------------task--------------------------------------------------------------------

    /**
     * 新增定时任务
     *
     * @param request {@link TaskRequest}
     */
    @Override
    public ZhongHeResult<String> addTimingTask(TaskRequest request) {
        final String generator = TimingFileTask.getInstance().generator("FE", request);
        return SendClient.getSingleton().send(CmdEnum.PRO_TIMING_TASK, "01", generator.toUpperCase()).toZhongHeResult();
    }

    /**
     * 编辑任务
     *
     * @param id      id
     * @param request {@link TaskRequest}
     */
    @Override
    public ZhongHeResult<?> editTimingTask(String id, TaskRequest request) {
        final String generator = TimingFileTask.getInstance().generator(id, request);
        return SendClient.getSingleton().send(CmdEnum.PRO_TIMING_TASK, "02", generator).toZhongHeResult();
    }

    /**
     * 删除定时任务
     *
     * @param id id
     */
    @Override
    public ZhongHeResult<?> deleteTimingTask(String id, TaskRequest request) {
        final String generator = TimingFileTask.getInstance().generator(id, request);
        return SendClient.getSingleton().send(CmdEnum.PRO_TIMING_TASK, "03", generator).toZhongHeResult();
    }

    /**
     * 添加可编辑任务
     *
     * @param request {@link TaskRequest}
     * @return {@link ZhongHeResult}
     */
    @Override
    public ZhongHeResult<String> addEditableTask(TaskRequest request) {
        final String generator = EditableTask.getInstance().generator("00", request);
        return SendClient.getSingleton().send(CmdEnum.REQUEST_EDITABLE_TASK, "00", generator.toUpperCase()).toZhongHeResult();
    }

    /**
     * 取消任务
     *
     * @param subId subId
     * @return {@link ZhongHeResult}
     */
    @Override
    public ZhongHeResult<?> abortBySubId(String subId) {
        return null;
    }

    /**
     * 终止指定id的任务
     *
     * @param id id
     */
    @Override
    public ZhongHeResult<?> abortTaskBySubId(String id) {
        return SendClient.getSingleton().send(CmdEnum.ABORT_TASK_BY_SUB_ID, "00", ServiceUtils.changeOrder(id, 2)).toZhongHeResult();
    }

    //------------------------------download data----------------------------------------------------------------------------

    /**
     * 获取播放终端列表
     */
    @Override
    public ZhongHeResult<List<TerminalDataResponse>> getPlayersByNos() {
        return getDownloadData("01");
    }

    /**
     * 获取终端分组
     */
    @Override
    public ZhongHeResult<List<GroupDataResponse>> getTerminalGroups() {
        return getDownloadData("03");
    }

    /**
     * 获取媒体文件列表
     */
    @Override
    public ZhongHeResult<List<MediaFileDataResponse>> getMediaFiles() {
        return getDownloadData("04");
    }

    /**
     * 下载数据
     */
    private <T> ZhongHeResult<List<T>> getDownloadData(String para) {
        ZhongHeResult<List<T>> zhongHeResult = new ZhongHeResult<>();
        try {
            final ResultInternal resultInternal = SendClient.getSingleton().send(CmdEnum.DOWNLOAD_DATA, para, null);
            if (resultInternal.isSuccess()) {
                try {
                    SyncResultSupport.downloadResultDataCountDown.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                final ZhongHeDownloadResult zhongHeDownloadResult = SyncResultSupport.downloadParaResultMap.get(para);
                if (zhongHeDownloadResult.isSuccess()) {
                    final List<T> data = (List<T>) zhongHeDownloadResult.getData();
                    zhongHeResult.setData(data);
                } else {
                    zhongHeResult.setSuccess(false);
                    zhongHeResult.setErrorMsg(zhongHeDownloadResult.getErrorMsg());
                }
            } else {
                zhongHeResult.setSuccess(false);
                zhongHeResult.setErrorMsg(resultInternal.getErrorMsg());
            }
        } finally {
            SyncResultSupport.downloadParaResultMap.remove(para);
        }
        return zhongHeResult;
    }
}