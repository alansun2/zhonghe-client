package vip.tuoyang.test;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import vip.tuoyang.zhonghe.bean.ZhongHeResult;
import vip.tuoyang.zhonghe.bean.request.TaskRequest;
import vip.tuoyang.zhonghe.bean.response.MediaFileDataResponse;
import vip.tuoyang.zhonghe.config.ZhongHeConfig;
import vip.tuoyang.zhonghe.service.SendClient;
import vip.tuoyang.zhonghe.service.ZhongHeControlClient;
import vip.tuoyang.zhonghe.service.ZhongHeServiceClient;

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
    private static ZhongHeControlClient zhongHeControlClient;

    private static ZhongHeServiceClient zhongHeServiceClient;

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
        zhongHeControlClient = new ZhongHeControlClient(zhongHeConfig);
        zhongHeServiceClient = new ZhongHeServiceClient();
        Thread.sleep(5000);
    }

    @Test
    public void init() {
        zhongHeControlClient.initMiddleWare();
        LockSupport.park();
    }

    @Test
    public void close() {
        zhongHeControlClient.close();
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
        zhongHeServiceClient.addTimingTask(taskRequest);
    }

    @Test
    public void getMediaFiles() {
        zhongHeServiceClient.getMediaFiles();
        LockSupport.park();
    }

    @Test
    public void testAll() {
        zhongHeControlClient.initMiddleWare();
        final ZhongHeResult<List<MediaFileDataResponse>> mediaFiles = zhongHeServiceClient.getMediaFiles();
        if (mediaFiles.isSuccess()) {
            final List<MediaFileDataResponse> data = mediaFiles.getData();
            data.forEach(mediaFileDataResponse -> System.out.println(mediaFileDataResponse.getNo() + ":" + mediaFileDataResponse.getName()));
        }
    }
}
