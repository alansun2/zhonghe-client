package vip.tuoyang.zhonghe.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import vip.tuoyang.base.exception.BizException;
import vip.tuoyang.base.util.AssertUtils;
import vip.tuoyang.zhonghe.bean.ResultInternal;
import vip.tuoyang.zhonghe.bean.ZhongHeDownloadResult;
import vip.tuoyang.zhonghe.bean.ZhongHeResult;
import vip.tuoyang.zhonghe.bean.request.TaskRequest;
import vip.tuoyang.zhonghe.bean.response.GroupDataResponse;
import vip.tuoyang.zhonghe.bean.response.MediaFileDataResponse;
import vip.tuoyang.zhonghe.bean.response.StateResponse;
import vip.tuoyang.zhonghe.bean.response.TerminalDataResponse;
import vip.tuoyang.zhonghe.config.ZhongHeConfig;
import vip.tuoyang.zhonghe.config.ZhongHeSystemProperties;
import vip.tuoyang.zhonghe.constants.CmdEnum;
import vip.tuoyang.zhonghe.constants.StateEnum;
import vip.tuoyang.zhonghe.exception.TimeOutException;
import vip.tuoyang.zhonghe.service.task.EditableTask;
import vip.tuoyang.zhonghe.service.task.TimingFileTask;
import vip.tuoyang.zhonghe.support.SyncResultSupport;
import vip.tuoyang.zhonghe.support.ZhongHeCallback;
import vip.tuoyang.zhonghe.utils.ConvertCode;
import vip.tuoyang.zhonghe.utils.ZhongHeUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * @author AlanSun
 * @date 2021/8/22 12:42
 **/
@Slf4j
public class ZhongHeClientImpl implements ZhongHeClient {
    private final ZhongHeConfig zhongHeConfig;

    private final SendClient sendClient;

    private final String label;

    public ZhongHeClientImpl(ZhongHeConfig zhongHeConfig, SendClient sendClient, String label) {
        zhongHeConfig.valid();
        this.zhongHeConfig = zhongHeConfig;
        this.sendClient = sendClient;
        this.label = label;
    }

    public static ZhongHeClient create(ZhongHeConfig zhongHeConfig, String label, ZhongHeCallback callback) {
        return new ZhongHeClientImpl(zhongHeConfig, new SendClient(zhongHeConfig, label, callback), label);
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
     * ??????????????????
     */
    @Override
    public ZhongHeResult<?> initMiddleWare(boolean needClose) {
        // ?????????
        if (needClose) {
            this.close(false);
        }

        //// ?????????????????????
        StringBuilder sb = new StringBuilder();
        // 16 17 deviceId
        sb.append(ZhongHeUtils.changeOrder(zhongHeConfig.getDeviceId(), 2));
        // 18 19 ?????????
        sb.append(ZhongHeUtils.changeOrder(zhongHeConfig.getManagerCode(), 2));
        // 20 - 51??????
        sb.append(StringUtils.rightPad(ConvertCode.intToHexString(zhongHeConfig.getDeviceDes().length() * 2, 1) + ConvertCode.bytes2HexString(ZhongHeUtils.toGbkBytes(zhongHeConfig.getDeviceDes())).toUpperCase(), 64, '0'));
        // 52 53 54, 55 nas ip
        final String nasIp = zhongHeConfig.getNasIp();
        final String[] nasIpSplit = nasIp.split("\\.");
        for (String nasIpSegment : nasIpSplit) {
            sb.append(ConvertCode.intToHexString(Integer.parseInt(nasIpSegment), 1).toUpperCase());
        }
        // 56 57 ????????????
        sb.append(ConvertCode.int2HexLittle(zhongHeConfig.getNasConnectPort()));
        // 58 59 ????????????
        sb.append(ConvertCode.int2HexLittle(zhongHeConfig.getNasControlPort()));
        // 60 61 ????????????
        sb.append(ConvertCode.int2HexLittle(zhongHeConfig.getNasCapturePort()));
        // 62 63
        sb.append("0000");

        return sendClient.send(CmdEnum.INIT, "00", sb.toString()).toZhongHeResult();
    }

    /**
     * ??????
     */
    @Override
    public ZhongHeResult<?> close(boolean isCloseChannel) {
        try {
            return sendClient.send(CmdEnum.CLOSE, "00", null).toZhongHeResult();
        } catch (Exception e) {
            if (e instanceof TimeOutException) {
                log.warn("close ?????????????????????????????????: label: [{}]", zhongHeConfig.getLabel());
                ZhongHeResult<Object> zhongHeResult = new ZhongHeResult<>();
                zhongHeResult.setSuccess(false);
                return zhongHeResult;
            }
            throw e;
        } finally {
            if (isCloseChannel) {
                sendClient.close();
            }
        }
    }

    /**
     * ????????????
     * <p>
     * 01AA: ??????
     * 00AA: NAS ??????
     */
    @Override
    public ZhongHeResult<StateResponse> state() {
        int retryCount = 3;
        ZhongHeResult<StateResponse> zhongHeResult = null;
        do {
            try {
                zhongHeResult = sendClient.send(CmdEnum.STATE, "02", null).toZhongHeResult();
            } catch (Exception e) {
                if (e instanceof TimeOutException) {
                    if (retryCount == 2) {
                        LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(1));
                        this.initMiddleWare(true);
                    }
                } else {
                    throw e;
                }
                retryCount--;
            }
        } while (zhongHeResult == null && retryCount > 0);

        if (zhongHeResult == null) {
            throw new BizException("??????????????????");
        }

        if (zhongHeResult.getData().getState().equals(StateEnum.OFFLINE_DOWN)) {
            this.initMiddleWare(true);
            LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(1));
            zhongHeResult = this.state();
        }
        return zhongHeResult;
    }

    //------------------------------------------task--------------------------------------------------------------------

    /**
     * ??????????????????
     *
     * @param request {@link TaskRequest}
     */
    @Override
    public ZhongHeResult<String> addTimingTask(TaskRequest request) {
        request.valid();
        final String generator = TimingFileTask.getInstance().generator("FE", request);
        return sendClient.send(CmdEnum.PRO_TIMING_TASK, "01", generator.toUpperCase()).toZhongHeResult();
    }

    /**
     * ????????????
     *
     * @param id      id
     * @param request {@link TaskRequest}
     */
    @Override
    public ZhongHeResult<String> editTimingTask(String id, TaskRequest request) {
        request.valid();
        AssertUtils.notBlank(id, "id null error");
        final String generator = TimingFileTask.getInstance().generator(id, request);
        return sendClient.send(CmdEnum.PRO_TIMING_TASK, "02", generator).toZhongHeResult();
    }

    /**
     * ??????????????????
     *
     * @param id id
     */
    @Override
    public ZhongHeResult<?> deleteTimingTask(String id, TaskRequest request) {
        request.valid();
        AssertUtils.notBlank(id, "id null error");
        final String generator = TimingFileTask.getInstance().generator(id, request);
        return sendClient.send(CmdEnum.PRO_TIMING_TASK, "03", generator).toZhongHeResult();
    }

    /**
     * ?????????????????????
     *
     * @param request {@link TaskRequest}
     * @return {@link ZhongHeResult}
     */
    @Override
    public ZhongHeResult<String> addEditableTask(TaskRequest request) {
        request.valid();
        final String generator = EditableTask.getInstance().generator("00", request);
        return sendClient.send(CmdEnum.REQUEST_EDITABLE_TASK, "00", generator.toUpperCase()).toZhongHeResult();
    }

    /**
     * ????????????id?????????
     *
     * @param id id
     */
    @Override
    public ZhongHeResult<?> abortTaskBySubId(String id) {
        AssertUtils.notBlank(id, "id null error");
        return sendClient.send(CmdEnum.ABORT_TASK_BY_SUB_ID, "00", ZhongHeUtils.changeOrder(id, 2)).toZhongHeResult();
    }

    //-----------------------------upload-------------------------------------------------------------------------------

    /**
     * ????????????
     *
     * @param filePath filePath
     * @return ???????????? id
     */
    @Override
    public ZhongHeResult<String> uploadMediaFile(String filePath) {
        AssertUtils.notBlank(filePath, "filePath null error");

        ZhongHeResult<String> zhongHeResult = new ZhongHeResult<>();
        // ??? 16 ??????
        final String filePathHex = ConvertCode.bytes2HexString(ZhongHeUtils.toGbkBytes(filePath));
        String content = "0000" + ConvertCode.intToHexString(filePathHex.length() / 2, 1) + filePathHex;
        final ResultInternal resultInternal = sendClient.send(CmdEnum.UPLOAD_MEDIA_FILE, "00", content);
        if (resultInternal.isSuccess()) {
            zhongHeResult.setData(resultInternal.getData().toString());
        } else {
            zhongHeResult.setSuccess(false);
            zhongHeResult.setErrorMsg(resultInternal.getErrorMsg());
        }

        return zhongHeResult;
    }

    /**
     * ??????????????????
     *
     * @param fileId   fileId
     * @param filePath filePath
     * @return {@link ZhongHeResult}
     */
    @Override
    public ZhongHeResult<?> deleteMediaFile(String fileId, String filePath) {
        String filePathHex = ConvertCode.bytes2HexString(ZhongHeUtils.toGbkBytes(filePath)).toUpperCase();
        if ((filePathHex.length() | 1) == 1) {
            filePathHex = filePathHex + 0;
        }
        String content = ZhongHeUtils.changeOrder(fileId, 2) + ConvertCode.intToHexString(filePathHex.length() / 2, 1) + filePathHex;
        return sendClient.send(CmdEnum.DELETE_MEDIA_FILE, "00", content).toZhongHeResult();
    }

    //------------------------------download data-----------------------------------------------------------------------

    /**
     * ????????????????????????
     */
    @Override
    public ZhongHeResult<List<TerminalDataResponse>> getPlayersByNos() {
        return getDownloadData("01");
    }

    /**
     * ??????????????????
     */
    @Override
    public ZhongHeResult<List<GroupDataResponse>> getTerminalGroups() {
        return getDownloadData("03");
    }

    /**
     * ????????????????????????
     */
    @Override
    public ZhongHeResult<List<MediaFileDataResponse>> getMediaFiles() {
        return getDownloadData("04");
    }

    /**
     * ????????????
     */
    private <T> ZhongHeResult<List<T>> getDownloadData(String para) {
        ZhongHeResult<List<T>> zhongHeResult = new ZhongHeResult<>();
        try {
            SyncResultSupport.getLabelDownloadResultDataCount(label).reset();
            final ResultInternal resultInternal = sendClient.send(CmdEnum.DOWNLOAD_DATA, para, null);
            if (resultInternal.isSuccess()) {
                try {
                    SyncResultSupport.getLabelDownloadResultDataCount(label).await(ZhongHeSystemProperties.timeout, TimeUnit.SECONDS);
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