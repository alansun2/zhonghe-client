package vip.tuoyang.zhonghe.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import vip.tuoyang.base.util.HttpClientUtils;
import vip.tuoyang.base.util.bean.HttpParams;
import vip.tuoyang.zhonghe.bean.ResultInternal;
import vip.tuoyang.zhonghe.bean.ZhongHeDownloadResult;
import vip.tuoyang.zhonghe.bean.ZhongHeResult;
import vip.tuoyang.zhonghe.bean.request.TaskRequest;
import vip.tuoyang.zhonghe.bean.response.GroupDataResponse;
import vip.tuoyang.zhonghe.bean.response.MediaFileDataResponse;
import vip.tuoyang.zhonghe.bean.response.TerminalDataResponse;
import vip.tuoyang.zhonghe.config.ZhongHeConfig;
import vip.tuoyang.zhonghe.config.ZhongHeSystemProperties;
import vip.tuoyang.zhonghe.constants.CmdEnum;
import vip.tuoyang.zhonghe.service.task.EditableTask;
import vip.tuoyang.zhonghe.service.task.TimingFileTask;
import vip.tuoyang.zhonghe.support.StateCallback;
import vip.tuoyang.zhonghe.support.SyncResultSupport;
import vip.tuoyang.zhonghe.support.ZhongHeClientLockProxy;
import vip.tuoyang.zhonghe.utils.ConvertCode;
import vip.tuoyang.zhonghe.utils.ServiceUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.List;

/**
 * @author AlanSun
 * @date 2021/8/22 12:42
 **/
@Slf4j
public class ZhongHeClientImpl implements ZhongHeClient {
    private final ZhongHeConfig zhongHeConfig;

    private final SendClient sendClient;

    public ZhongHeClientImpl(ZhongHeConfig zhongHeConfig, SendClient sendClient) {
        zhongHeConfig.valid();
        this.zhongHeConfig = zhongHeConfig;
        this.sendClient = sendClient;
    }

    public static ZhongHeClient create(ZhongHeConfig zhongHeConfig, String label, StateCallback stateCallback) {
        return new ZhongHeClientImpl(zhongHeConfig, new SendClient(zhongHeConfig, label, stateCallback));
    }

    @Override
    public SendClient getSendClient() {
        return sendClient;
    }

    /**
     * test
     */
    @Override
    public ZhongHeResult<?> test() {
        return sendClient.send(CmdEnum.TEST, "00", null).toZhongHeResult();
    }

    /**
     * 初始化中间件
     */
    @Override
    public ZhongHeResult<?> initMiddleWare() {
        // 先关闭
        this.close(false);

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

        return sendClient.send(CmdEnum.INIT, "00", sb.toString()).toZhongHeResult();
    }

    /**
     * 关闭
     */
    @Override
    public ZhongHeResult<?> close(boolean isCloseChannel) {
        final ZhongHeResult<Object> objectZhongHeResult = sendClient.send(CmdEnum.CLOSE, "00", null).toZhongHeResult();
        if (isCloseChannel) {
            sendClient.close();
        }
        return objectZhongHeResult;
    }

    /**
     * 获取状态
     * <p>
     * 01AA: 正常
     * 00AA: NAS 挂了
     */
    @Override
    public ZhongHeResult<Byte> state() {
        return sendClient.send(CmdEnum.STATE, "02", null).toZhongHeResult();
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
        return sendClient.send(CmdEnum.PRO_TIMING_TASK, "01", generator.toUpperCase()).toZhongHeResult();
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
        return sendClient.send(CmdEnum.PRO_TIMING_TASK, "02", generator).toZhongHeResult();
    }

    /**
     * 删除定时任务
     *
     * @param id id
     */
    @Override
    public ZhongHeResult<?> deleteTimingTask(String id, TaskRequest request) {
        final String generator = TimingFileTask.getInstance().generator(id, request);
        return sendClient.send(CmdEnum.PRO_TIMING_TASK, "03", generator).toZhongHeResult();
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
        return sendClient.send(CmdEnum.REQUEST_EDITABLE_TASK, "00", generator.toUpperCase()).toZhongHeResult();
    }

    /**
     * 终止指定id的任务
     *
     * @param id id
     */
    @Override
    public ZhongHeResult<?> abortTaskBySubId(String id) {
        return sendClient.send(CmdEnum.ABORT_TASK_BY_SUB_ID, "00", ServiceUtils.changeOrder(id, 2)).toZhongHeResult();
    }

    //-----------------------------upload-------------------------------------------------------------------------------

    /**
     * 上传文件
     *
     * @param inputStream inputStream
     * @param fileName    fileName
     * @return 媒体文件 id
     */
    @Override
    public ZhongHeResult<String> uploadMediaFile(InputStream inputStream, String fileName) {
        ZhongHeResult<String> zhongHeResult = new ZhongHeResult<>();
        HttpResponse httpResponse;
        try {
            BasicHeader[] basicHeaders = new BasicHeader[1];
            basicHeaders[0] = new BasicHeader("secret", ZhongHeSystemProperties.secret);
            String uploadUrl = zhongHeConfig.getFileUploadUrl() + "?fileName=" + URLEncoder.encode(fileName, Charset.defaultCharset().toString());
            final InputStreamEntity inputStreamEntity = new InputStreamEntity(inputStream);
            final HttpParams httpParams = HttpParams.builder().url(uploadUrl).httpEntity(inputStreamEntity).headers(basicHeaders).build();
            httpResponse = HttpClientUtils.doPost(httpParams);
        } catch (IOException e) {
            log.error("上传文件失败, 请求失败", e);
            zhongHeResult.setSuccess(false);
            zhongHeResult.setErrorMsg("上传文件失败:" + (e.getMessage().length() > 50 ? e.getMessage().substring(0, 50) : e.getMessage()));
            return zhongHeResult;
        }

        final StatusLine statusLine = httpResponse.getStatusLine();
        if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
            String filePath;
            try {
                filePath = EntityUtils.toString(httpResponse.getEntity());
            } catch (IOException e) {
                log.error("上传文件失败, 获取文件路径失败", e);
                zhongHeResult.setSuccess(false);
                zhongHeResult.setErrorMsg("上传文件失败2:" + (e.getMessage().length() > 50 ? e.getMessage().substring(0, 50) : e.getMessage()));
                return zhongHeResult;
            }
            // 转 16 进制
            final String filePathHex = ConvertCode.bytes2HexString(ServiceUtils.toGbkBytes(filePath));
            String content = "0000" + ConvertCode.intToHexString(filePathHex.length() / 2, 1) + filePathHex;
            final ResultInternal resultInternal = sendClient.send(CmdEnum.UPLOAD_MEDIA_FILE, "00", content);
            if (resultInternal.isSuccess()) {
                zhongHeResult.setData(resultInternal.getData().toString());
            } else {
                zhongHeResult.setSuccess(false);
                zhongHeResult.setErrorMsg(resultInternal.getErrorMsg());
            }
        } else {
            zhongHeResult.setSuccess(false);
            zhongHeResult.setErrorMsg("上传文件失败1");
        }

        return zhongHeResult;
    }

    /**
     * 删除媒体文件
     *
     * @param fileId fileId
     * @return {@link ZhongHeResult}
     */
    @Override
    public ZhongHeResult<?> deleteMediaFile(String fileId, String fileName) {
        String fileNameHex = ConvertCode.bytes2HexString(ServiceUtils.toGbkBytes(fileName)).toUpperCase();
        if ((fileNameHex.length() | 1) == 1) {
            fileNameHex = fileNameHex + 0;
        }
        String content = ServiceUtils.changeOrder(fileId, 2) + ConvertCode.intToHexString(fileNameHex.length() / 2, 1) + fileNameHex;
        return sendClient.send(CmdEnum.DELETE_MEDIA_FILE, "00", content).toZhongHeResult();
    }

    //------------------------------download data-----------------------------------------------------------------------

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
            final ResultInternal resultInternal = sendClient.send(CmdEnum.DOWNLOAD_DATA, para, null);
            if (resultInternal.isSuccess()) {
                try {
                    SyncResultSupport.labelDownloadResultDataCountDown.get(ZhongHeClientLockProxy.LABEL_THREAD_LOCAL.get()).await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                final ZhongHeDownloadResult zhongHeDownloadResult = SyncResultSupport.labelDownloadResultMap.get(para);
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
            SyncResultSupport.labelDownloadResultMap.remove(para);
        }
        return zhongHeResult;
    }
}