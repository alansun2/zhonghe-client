package vip.tuoyang.test;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import vip.tuoyang.zhonghe.bean.ZhongHeResult;
import vip.tuoyang.zhonghe.bean.request.TaskRequest;
import vip.tuoyang.zhonghe.bean.response.MediaFileDataResponse;
import vip.tuoyang.zhonghe.config.ZhongHeConfig;
import vip.tuoyang.zhonghe.service.SendClient;
import vip.tuoyang.zhonghe.service.ZhongHeClient;
import vip.tuoyang.zhonghe.service.ZhongHeClientImpl;
import vip.tuoyang.zhonghe.support.ZhongHeClientLockProxy;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.LockSupport;

/**
 * @author AlanSun
 * @date 2021/8/23 14:37
 */
public class TestService {
    private static ZhongHeClient zhongHeClient;

    @BeforeAll
    public static void before() throws InterruptedException {
        ZhongHeConfig zhongHeConfig = new ZhongHeConfig();
        zhongHeConfig.setLocalBindPort(7654);
        zhongHeConfig.setDeviceDes("Alan本地");
        zhongHeConfig.setDeviceId("00001011");
        zhongHeConfig.setManagerCode("12345668");
        zhongHeConfig.setMiddleWareIp("192.168.31.252");
        zhongHeConfig.setMiddleWarePort(8607);
        zhongHeConfig.setNasIp("192.168.31.252");
        zhongHeConfig.setNasConnectPort(8100);
        zhongHeConfig.setNasControlPort(8101);
        zhongHeConfig.setNasCapturePort(8201);
        SendClient.init(zhongHeConfig);
        zhongHeClient = ZhongHeClientLockProxy.getProxy(new ZhongHeClientImpl(zhongHeConfig));
        Thread.sleep(5000);
    }

    @Test
    public void init() {
        zhongHeClient.initMiddleWare();
    }

    @Test
    public void close() {
        zhongHeClient.close();
    }

    /**
     * 发送定时任务
     */
    @Test
    public void sendTimingTask() {
        TaskRequest taskRequest = new TaskRequest();
        taskRequest.setTaskName("testttt");
        taskRequest.setPlayMode((byte) 3);
        taskRequest.setStartTime(LocalDateTime.of(LocalDate.now(), LocalTime.of(12, 0)));
        taskRequest.setTaskType((byte) 1);
        taskRequest.setWeekOption("1,3,5,7");
        taskRequest.setPlayContentIdList(Collections.singletonList("0001"));
        taskRequest.setPlayObjectIdList(Collections.singletonList("00001000"));
        zhongHeClient.addTimingTask(taskRequest);
    }

    @Test
    public void getMediaFiles() {
        zhongHeClient.getMediaFiles();
        LockSupport.park();
    }

    @Test
    public void testAll() {
        final ZhongHeResult<List<MediaFileDataResponse>> mediaFiles = zhongHeClient.getMediaFiles();
        if (mediaFiles.isSuccess()) {
            final List<MediaFileDataResponse> data = mediaFiles.getData();
            data.forEach(mediaFileDataResponse -> System.out.println(mediaFileDataResponse.getNo() + ":" + mediaFileDataResponse.getMediaFileName()));
        }
    }

    @Test
    public void testAddTimingTask() {
        TaskRequest request = new TaskRequest();
        request.setTaskType((byte) 0);
        request.setTaskName("test");
        request.setPlayMode((byte) 0);
        request.setStartTime(LocalDateTime.of(2021, 9, 1, 0, 0, 0));
        request.setEndTime(LocalDateTime.of(2021, 9, 1, 0, 10, 0));
        request.setTimeType((byte) 1);
        request.setTimeMode((byte) 0);
        request.setWeekOption("1,2,5");
        request.setPlayContentIdList(Collections.singletonList("0001"));
        request.setPlayObjectIdList(Collections.singletonList("00001000"));
        final ZhongHeResult<String> stringZhongHeResult = zhongHeClient.addTimingTask(request);
        System.out.println(stringZhongHeResult.getData());
    }

    @Test
    public void editTimingTask() {
        TaskRequest request = new TaskRequest();
        request.setTaskType((byte) 0);
        request.setTaskName("test");
        request.setPlayMode((byte) 0);
        request.setStartTime(LocalDateTime.of(2021, 9, 1, 11, 0, 0));
        request.setEndTime(LocalDateTime.of(2021, 9, 1, 11, 10, 0));
        request.setTimeType((byte) 1);
        request.setTimeMode((byte) 0);
        request.setWeekOption("1,4,5");
        request.setPlayContentIdList(Collections.singletonList("0001"));
        request.setPlayObjectIdList(Collections.singletonList("00001000"));
        final ZhongHeResult<?> zhongHeResult = zhongHeClient.editTimingTask("06", request);
    }

    @Test
    public void deleteTimingTask() {
        TaskRequest request = new TaskRequest();
        request.setTaskType((byte) 0);
        request.setTaskName("test");
        request.setPlayMode((byte) 0);
        request.setStartTime(LocalDateTime.of(2021, 9, 1, 0, 0, 0));
        request.setEndTime(LocalDateTime.of(2021, 9, 1, 0, 10, 0));
        request.setTimeType((byte) 1);
        request.setTimeMode((byte) 0);
        request.setWeekOption("1,2,5");
        request.setPlayContentIdList(Collections.singletonList("0001"));
        request.setPlayObjectIdList(Collections.singletonList("00001000"));
        zhongHeClient.deleteTimingTask("06", request);
    }
}
