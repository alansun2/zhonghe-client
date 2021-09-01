package vip.tuoyang.test;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import vip.tuoyang.zhonghe.bean.ZhongHeResult;
import vip.tuoyang.zhonghe.bean.request.TaskRequest;
import vip.tuoyang.zhonghe.bean.response.GroupDataResponse;
import vip.tuoyang.zhonghe.bean.response.MediaFileDataResponse;
import vip.tuoyang.zhonghe.bean.response.TerminalDataResponse;
import vip.tuoyang.zhonghe.config.ZhongHeConfig;
import vip.tuoyang.zhonghe.service.ZhongHeClient;
import vip.tuoyang.zhonghe.service.ZhongHeConnectionManager;
import vip.tuoyang.zhonghe.support.StateCallback;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.LockSupport;

/**
 * @author AlanSun
 * @date 2021/8/23 14:37
 */
public class TestService {
    private static ZhongHeClient zhongHeClient;

    private static final ZhongHeConnectionManager zhongHeConnectionManager = new ZhongHeConnectionManager(new StateCallback.DefaultCallback());

    @BeforeAll
    public static void before() throws InterruptedException {
        ZhongHeConfig zhongHeConfig = new ZhongHeConfig();
        String localIp = "192.168.31.252";
        zhongHeConfig.setLocalBindPort(7660);
        zhongHeConfig.setDeviceDes("Alan本地");
        zhongHeConfig.setDeviceId("00001011");
        zhongHeConfig.setManagerCode("12345668");
        zhongHeConfig.setMiddleWareIp(localIp);
        zhongHeConfig.setMiddleWarePort(8607);
        zhongHeConfig.setMiddleWareCapturePort(8608);
        zhongHeConfig.setNasIp(localIp);
        zhongHeConfig.setNasConnectPort(8100);
        zhongHeConfig.setNasControlPort(8101);
        zhongHeConfig.setNasCapturePort(8201);

        zhongHeConnectionManager.initConnection(Collections.singletonMap("test", zhongHeConfig));
        zhongHeClient = zhongHeConnectionManager.getZhongHeClient("test");
        Thread.sleep(2000);
    }

    @Test
    public void init() {
        zhongHeClient.initMiddleWare();
    }

    @Test
    public void state() {
        zhongHeClient.state();
        LockSupport.park();
    }

    @Test
    public void testGetMediaFiles() {
        final ZhongHeResult<List<MediaFileDataResponse>> mediaFiles = zhongHeClient.getMediaFiles();
        if (mediaFiles.isSuccess()) {
            final List<MediaFileDataResponse> data = mediaFiles.getData();
            data.forEach(mediaFileDataResponse -> System.out.println(mediaFileDataResponse.getNo() + ":" + mediaFileDataResponse.getMediaFileName()));
        }
    }

    @Test
    public void testGetGroups() {
        final ZhongHeResult<List<GroupDataResponse>> terminalGroups = zhongHeClient.getTerminalGroups();
        if (terminalGroups.isSuccess()) {
            final List<GroupDataResponse> data = terminalGroups.getData();
            data.forEach(mediaFileDataResponse -> System.out.println(mediaFileDataResponse.getNo() + ":" + mediaFileDataResponse.getGroupName() + ": [" + String.join(",", mediaFileDataResponse.getTerminalNos()) + "}"));
        }
    }

    @Test
    public void getPlayers() {
        final ZhongHeResult<List<TerminalDataResponse>> playersByNos = zhongHeClient.getPlayersByNos();
        if (playersByNos.isSuccess()) {
            final List<TerminalDataResponse> data = playersByNos.getData();
            data.forEach(mediaFileDataResponse -> System.out.println(mediaFileDataResponse.getTerminalNo() + ":" + mediaFileDataResponse.getTerminalName() + ":" + mediaFileDataResponse.getTerminalStatus()));
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

    @Test
    public void instantTask() {
        TaskRequest request = new TaskRequest();
        request.setTaskType((byte) 0);
        request.setTaskName("test2");
        request.setPlayMode((byte) 3);
        request.setCount((byte) 2);
        request.setTimeMode((byte) 2);
        request.setTimeType((byte) 0);
        request.setPlayContentIdList(Collections.singletonList("0001"));
        request.setPlayObjectIdList(Collections.singletonList("FFFFFF01"));
        final ZhongHeResult<String> stringZhongHeResult = zhongHeClient.addEditableTask(request);
        System.out.println(stringZhongHeResult.getData());
    }

    @Test
    public void instantTask1() {
        TaskRequest request = new TaskRequest();
        request.setTaskType((byte) 0);
        request.setTaskName("test3");
        request.setPlayMode((byte) 3);
        request.setVolume(3);
        request.setTimeMode((byte) 1);
        request.setTimeType((byte) 1);
        request.setStartTime(LocalDateTime.of(2021, 9, 1, 15, 30, 0));
        request.setEndTime(LocalDateTime.of(2021, 9, 1, 15, 40, 0));
        request.setPlayContentIdList(Collections.singletonList("0001"));
        request.setPlayObjectIdList(Collections.singletonList("FFFFFF01"));
        final ZhongHeResult<String> stringZhongHeResult = zhongHeClient.addEditableTask(request);
        System.out.println(stringZhongHeResult.getData());
    }

    @Test
    public void abortTask(){
        final ZhongHeResult<?> zhongHeResult = zhongHeClient.abortTaskBySubId("D8E3F001");
    }

    @Test
    public void deleteMediaFile(){
        zhongHeClient.deleteMediaFile("001E", "周杰伦 - 退后.mp3");
    }
}
