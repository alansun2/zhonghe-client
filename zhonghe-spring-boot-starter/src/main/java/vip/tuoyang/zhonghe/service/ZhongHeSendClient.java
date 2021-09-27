package vip.tuoyang.zhonghe.service;

import io.netty.channel.Channel;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vip.tuoyang.base.exception.BizException;
import vip.tuoyang.base.util.AssertUtils;
import vip.tuoyang.zhonghe.bean.SoftUpdateRequest;
import vip.tuoyang.zhonghe.bean.SoftUpdateResponse;
import vip.tuoyang.zhonghe.bean.ZhongHeDto;
import vip.tuoyang.zhonghe.bean.ZhongHeResult;
import vip.tuoyang.zhonghe.bean.request.FileUploadRequest;
import vip.tuoyang.zhonghe.bean.request.Task1Request;
import vip.tuoyang.zhonghe.bean.response.GroupDataResponse;
import vip.tuoyang.zhonghe.bean.response.StateResponse;
import vip.tuoyang.zhonghe.bean.response.TerminalDataResponse;
import vip.tuoyang.zhonghe.config.properties.ServiceSystemProperties;
import vip.tuoyang.zhonghe.support.SyncSupport;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author AlanSun
 * @date 2021/9/22 14:39
 */
@Service
public class ZhongHeSendClient {
    @Autowired
    private ServiceSystemProperties serviceSystemProperties;

    public ZhongHeResult<?> init(String label) {
        ZhongHeDto<Boolean> zhongHeBaseRequest = new ZhongHeDto<>();
        zhongHeBaseRequest.setCommand((byte) 1);
        zhongHeBaseRequest.setData(Boolean.TRUE);
        return this.getResult(label, zhongHeBaseRequest);
    }

    public ZhongHeResult<StateResponse> state(String label) {
        ZhongHeDto<String> zhongHeBaseRequest = new ZhongHeDto<>();
        zhongHeBaseRequest.setCommand((byte) 2);
        zhongHeBaseRequest.setData(label);
        return (ZhongHeResult<StateResponse>) this.getResult(label, zhongHeBaseRequest);
    }

    public ZhongHeResult<String> addTimingTask(String label, Task1Request taskRequest) {
        ZhongHeDto<Task1Request> zhongHeBaseRequest = new ZhongHeDto<>();
        zhongHeBaseRequest.setCommand((byte) 3);
        zhongHeBaseRequest.setData(taskRequest);
        return (ZhongHeResult<String>) this.getResult(label, zhongHeBaseRequest);
    }

    public ZhongHeResult<String> editTimingTask(String label, Task1Request taskRequest) {
        ZhongHeDto<Task1Request> zhongHeBaseRequest = new ZhongHeDto<>();
        zhongHeBaseRequest.setCommand((byte) 4);
        zhongHeBaseRequest.setData(taskRequest);
        return (ZhongHeResult<String>) this.getResult(label, zhongHeBaseRequest);
    }

    public ZhongHeResult<?> deleteTimingTask(String label, Task1Request taskRequest) {
        ZhongHeDto<Task1Request> zhongHeBaseRequest = new ZhongHeDto<>();
        zhongHeBaseRequest.setCommand((byte) 5);
        zhongHeBaseRequest.setData(taskRequest);
        return this.getResult(label, zhongHeBaseRequest);
    }

    public ZhongHeResult<String> addEditableTask(String label, Task1Request taskRequest) {
        ZhongHeDto<Task1Request> zhongHeBaseRequest = new ZhongHeDto<>();
        zhongHeBaseRequest.setCommand((byte) 6);
        zhongHeBaseRequest.setData(taskRequest);
        return (ZhongHeResult<String>) this.getResult(label, zhongHeBaseRequest);
    }

    public ZhongHeResult<?> abortTaskBySubId(String label, String taskNo) {
        ZhongHeDto<String> zhongHeBaseRequest = new ZhongHeDto<>();
        zhongHeBaseRequest.setCommand((byte) 7);
        zhongHeBaseRequest.setData(taskNo);
        return this.getResult(label, zhongHeBaseRequest);
    }

    public ZhongHeResult<String> uploadMediaFile(String label, String fileName, InputStream inputStream) {
        ZhongHeDto<FileUploadRequest> zhongHeBaseRequest = new ZhongHeDto<>();
        zhongHeBaseRequest.setCommand((byte) 8);
        final byte[] bytes;
        try {
            bytes = IOUtils.toByteArray(inputStream);
        } catch (IOException e) {
            throw new BizException("上传文件失败");
        }
        final String content = Base64.getEncoder().encodeToString(bytes);
        FileUploadRequest fileUploadRequest = new FileUploadRequest();
        fileUploadRequest.setFileName(fileName);
        fileUploadRequest.setContentBase64(content);
        zhongHeBaseRequest.setData(fileUploadRequest);
        return (ZhongHeResult<String>) this.getResult(label, zhongHeBaseRequest);
    }

    public ZhongHeResult<?> deleteMediaFile(String label, String fileNo, String fileName) {
        ZhongHeDto<FileUploadRequest> zhongHeBaseRequest = new ZhongHeDto<>();
        zhongHeBaseRequest.setCommand((byte) 9);
        FileUploadRequest fileUploadRequest = new FileUploadRequest();
        fileUploadRequest.setFileNo(fileNo);
        fileUploadRequest.setFileName(fileName);
        zhongHeBaseRequest.setData(fileUploadRequest);
        return this.getResult(label, zhongHeBaseRequest);
    }

    public ZhongHeResult<List<TerminalDataResponse>> getPlayersByNos(String label) {
        ZhongHeDto<String> zhongHeBaseRequest = new ZhongHeDto<>();
        zhongHeBaseRequest.setCommand((byte) 10);
        return (ZhongHeResult<List<TerminalDataResponse>>) this.getResult(label, zhongHeBaseRequest);
    }

    public ZhongHeResult<List<GroupDataResponse>> getTerminalGroups(String label) {
        ZhongHeDto<String> zhongHeBaseRequest = new ZhongHeDto<>();
        zhongHeBaseRequest.setCommand((byte) 11);
        return (ZhongHeResult<List<GroupDataResponse>>) this.getResult(label, zhongHeBaseRequest);
    }

    public boolean reboot(String label) {
        ZhongHeDto<String> zhongHeBaseRequest = new ZhongHeDto<>();
        zhongHeBaseRequest.setCommand((byte) 12);
        return this.getResult(label, zhongHeBaseRequest).isSuccess();
    }

    /**
     * 软件更新
     *
     * @param softUpdateRequest {@link }
     * @param labels            指定哪些机器需要更新
     * @return {@link SoftUpdateResponse}
     */
    public SoftUpdateResponse softUpdate(List<String> labels, SoftUpdateRequest softUpdateRequest) {
        AssertUtils.notEmpty(labels, "至少选择一台机器更新");
        softUpdateRequest.valid();
        SoftUpdateResponse softUpdateResponse = new SoftUpdateResponse();
        AtomicInteger successCount = new AtomicInteger();
        List<String> failLabels = new ArrayList<>();
        labels.forEach(item -> {
            ZhongHeDto<SoftUpdateRequest> zhongHeBaseRequest = new ZhongHeDto<>();
            zhongHeBaseRequest.setCommand((byte) 15);
            zhongHeBaseRequest.setData(softUpdateRequest);
            final ZhongHeResult<?> result = getResult(item, zhongHeBaseRequest);
            if (result.isSuccess()) {
                successCount.getAndIncrement();
            } else {
                failLabels.add(item);
            }
        });

        softUpdateResponse.setSuccessCount(successCount.get());
        softUpdateResponse.setFileResult(failLabels);
        return softUpdateResponse;
    }

    private ZhongHeResult<?> getResult(String label, ZhongHeDto<?> zhongHeBaseRequest) {
        getChannel(label).writeAndFlush(zhongHeBaseRequest);
        try {
            SyncSupport.getCountDownLatch2(label).await(serviceSystemProperties.getTimeout() * 5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new BizException("请求超时");
        }
        return SyncSupport.labelResultMap.get(label);
    }

    private Channel getChannel(String label) {
        final Channel channel = ServiceHandler.LABEL_CHANNEL_MAP.get(label);
        AssertUtils.notNull(channel, "中间件未启动");
        AssertUtils.isTrue(channel.isActive(), "连接已断开，请稍后重试");
        return channel;
    }
}
