package vip.tuoyang.test;

import com.alibaba.fastjson.JSON;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import vip.tuoyang.zhonghe.bean.ZhongHeResult;
import vip.tuoyang.zhonghe.bean.request.TaskRequest;
import vip.tuoyang.zhonghe.bean.response.GroupDataResponse;
import vip.tuoyang.zhonghe.bean.response.MediaFileDataResponse;
import vip.tuoyang.zhonghe.bean.response.StateResponse;
import vip.tuoyang.zhonghe.bean.response.TerminalDataResponse;
import vip.tuoyang.zhonghe.config.ZhongHeConfig;
import vip.tuoyang.zhonghe.service.ZhongHeClient;
import vip.tuoyang.zhonghe.service.ZhongHeConnectionManager;
import vip.tuoyang.zhonghe.support.ZhongHeCallback;

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

    @BeforeAll
    public static void before() throws InterruptedException {
        ZhongHeConfig zhongHeConfig = new ZhongHeConfig();
        String localIp = "192.168.166.153";
        zhongHeConfig.setLocalBindPort(7000);
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

        ZhongHeConnectionManager zhongHeConnectionManager = new ZhongHeConnectionManager(new ZhongHeCallback.DefaultCallback());
        zhongHeConnectionManager.initConnection(Collections.singletonMap("test", zhongHeConfig));
        zhongHeClient = zhongHeConnectionManager.getZhongHeClient("test");
        Thread.sleep(2000);
    }

    @Test
    public void init() {
        zhongHeClient.initMiddleWare(true);
    }

    @Test
    public void close() {
        zhongHeClient.close(false);
    }

    @Test
    public void state() {
        final ZhongHeResult<StateResponse> zhongHeResult = zhongHeClient.state();
        System.out.println("测试 state: " + zhongHeResult);
        LockSupport.park();
    }

    @Test
    public void test() {
        zhongHeClient.test();
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
        request.setTaskName("Input Name for Task");
        request.setPlayMode((byte) 3);
        request.setCount((byte) 1);
        request.setStartTime(LocalDateTime.of(2021, 9, 15, 9, 30, 0));
        request.setEndTime(LocalDateTime.of(2021, 9, 15, 9, 33, 9));
        request.setTimeMode((byte) 0);
        request.setTimeType((byte) 1);
        request.setWeekOption("1,2,3,4,5,6,7");
//        request.setIsOpenPowerAmplifierInAdvanceTime(3);
//        request.setIsOpenPowerAmplifierInAdvance((byte) 1);
        request.setPlayContentIdList(Collections.singletonList("0001"));
        request.setPlayObjectIdList(Collections.singletonList("FFFFFF01"));
//        String jsonStr = "{\"count\":3,\"endTime\":\"2022-09-30T09:57:20\",\"endTimeHex\":\"16091e093914\",\"isOpenPowerAmplifierInAdvance\":1,\"isOpenPowerAmplifierInAdvanceTime\":3,\"openInAdvanceTimeHex\":\"03\",\"playContent\":\"06000000\",\"playContentIdList\":[\"0006\"],\"playContentLen\":\"02\",\"playMode\":3,\"playObject\":\"01FFFFFF\",\"playObjectIdList\":[\"FFFFFF01\"],\"playObjectLen\":\"01\",\"playWayHex\":\"1b\",\"startTime\":\"2021-09-10T09:55:29\",\"startTimeHex\":\"15090a09371d\",\"taskName\":\"test3\",\"taskNameHex\":\"0574657374330000000000000000000000000000000000000000000000000000\",\"taskType\":0,\"taskTypeHex\":\"00\",\"time\":\"41\",\"timeMode\":0,\"timeType\":1,\"volume\":0,\"volumeHex\":\"00\",\"weekHex\":\"38\",\"weekOption\":\"3,4,5\"}";
//        final TaskRequest request = JSON.parseObject(jsonStr, TaskRequest.class);
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
//        TaskRequest request = new TaskRequest();
//        request.setTaskType((byte) 0);
//        request.setTaskName("test");
//        request.setPlayMode((byte) 0);
//        request.setStartTime(LocalDateTime.of(2021, 9, 1, 0, 0, 0));
//        request.setEndTime(LocalDateTime.of(2021, 9, 1, 0, 10, 0));
//        request.setTimeType((byte) 1);
//        request.setTimeMode((byte) 0);
//        request.setWeekOption("1,2,5");
//        request.setPlayContentIdList(Collections.singletonList("0001"));
//        request.setPlayObjectIdList(Collections.singletonList("00001000"));
        String jsonStr = "{\"count\":3,\"endTime\":\"2022-09-30T09:57:20\",\"endTimeHex\":\"16091e093914\",\"isOpenPowerAmplifierInAdvance\":1,\"isOpenPowerAmplifierInAdvanceTime\":3,\"openInAdvanceTimeHex\":\"03\",\"playContent\":\"06000000\",\"playContentIdList\":[\"0006\"],\"playContentLen\":\"02\",\"playMode\":3,\"playObject\":\"01FFFFFF\",\"playObjectIdList\":[\"FFFFFF01\"],\"playObjectLen\":\"01\",\"playWayHex\":\"1b\",\"startTime\":\"2021-09-10T09:55:29\",\"startTimeHex\":\"15090a09371d\",\"taskName\":\"test3\",\"taskNameHex\":\"0574657374330000000000000000000000000000000000000000000000000000\",\"taskType\":0,\"taskTypeHex\":\"00\",\"time\":\"41\",\"timeMode\":0,\"timeType\":1,\"volume\":0,\"volumeHex\":\"00\",\"weekHex\":\"38\",\"weekOption\":\"3,4,5\"}";
        final TaskRequest request = JSON.parseObject(jsonStr, TaskRequest.class);
        zhongHeClient.deleteTimingTask("02", request);
    }

    @Test
    public void instantTask() {
//        TaskRequest request = new TaskRequest();
//        request.setTaskType((byte) 0);
//        request.setTaskName("小程序立即播报2021-10-09 13:24:59");
//        request.setPlayMode((byte) 3);
//        request.setCount((byte) 2);
//        request.setTimeMode((byte) 2);
//        request.setTimeType((byte) 0);
//        request.setIsOpenPowerAmplifierInAdvance((byte) 1);
//        request.setIsOpenPowerAmplifierInAdvanceTime(3);
//        request.setPlayContentIdList(Collections.singletonList("0008"));
//        request.setPlayObjectIdList(Collections.singletonList("FFFFFF01"));
        String jsonStr = "{\"count\":1,\"endTime\":\"2021-10-09T14:00:33.853\",\"endTimeHex\":\"150a090e0021\",\"isOpenPowerAmplifierInAdvance\":0,\"isOpenPowerAmplifierInAdvanceTime\":0,\"openInAdvanceTimeHex\":\"00\",\"playContent\":\"11000000\",\"playContentIdList\":[\"0011\"],\"playContentLen\":\"02\",\"playMode\":3,\"playObject\":\"01FFFFFF\",\"playObjectIdList\":[\"FFFFFF01\"],\"playObjectLen\":\"01\",\"playWayHex\":\"0b\",\"startTime\":\"2021-10-09T14:00:31.853\",\"startTimeHex\":\"150a090e001f\",\"taskName\":\"小程序立即播报2021-10-09 14:00:28\",\"taskNameHex\":\"1fD0A1B3CCD0F2C1A2BCB4B2A5B1A8323032312D31302D30392031343A30303A\",\"taskType\":0,\"taskTypeHex\":\"00\",\"time\":\"20\",\"timeMode\":2,\"timeType\":0,\"volume\":0,\"volumeHex\":\"00\",\"weekHex\":\"00\"}";
        final TaskRequest request = JSON.parseObject(jsonStr, TaskRequest.class);
        final ZhongHeResult<String> stringZhongHeResult = zhongHeClient.addEditableTask(request);
        System.out.println(stringZhongHeResult.getData());
    }

    @Test
    public void instantTask1() {
        TaskRequest request = new TaskRequest();
        request.setTaskType((byte) 0);
        request.setTaskName("test");
        request.setPlayMode((byte) 3);
//        request.setCount((byte) 2);
        request.setTimeMode((byte) 1);
        request.setTimeType((byte) 1);
        request.setStartTime(LocalDateTime.of(2021, 9, 10, 15, 30, 0));
        request.setEndTime(LocalDateTime.of(2021, 9, 10, 15, 40, 0));
        request.setPlayContentIdList(Collections.singletonList("0001"));
        request.setPlayObjectIdList(Collections.singletonList("FFFFFF01"));
        final ZhongHeResult<String> stringZhongHeResult = zhongHeClient.addEditableTask(request);
        System.out.println(stringZhongHeResult.getData());
    }

    @Test
    public void abortTask() {
        final ZhongHeResult<?> zhongHeResult = zhongHeClient.abortTaskBySubId("D54DF001");
        System.out.println(zhongHeResult);
    }

    @Test
    public void uploadMediaFile() {
        final ZhongHeResult<String> zhongHeResult = zhongHeClient.uploadMediaFile("周杰伦 - 园游会.mp3");
        System.out.println(zhongHeResult.getData());
    }

    @Test
    public void deleteMediaFile() {
        final ZhongHeResult<?> zhongHeResult = zhongHeClient.deleteMediaFile("001E", "周杰伦 - 园游会.mp3");
        System.out.println(zhongHeResult);
    }
}
