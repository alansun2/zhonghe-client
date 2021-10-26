package vip.tuoyang.zhonghe.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vip.tuoyang.zhonghe.bean.ZhongHeResult;
import vip.tuoyang.zhonghe.bean.request.*;
import vip.tuoyang.zhonghe.bean.response.GroupDataResponse;
import vip.tuoyang.zhonghe.bean.response.SoftUpdateResponse;
import vip.tuoyang.zhonghe.bean.response.StateResponse;
import vip.tuoyang.zhonghe.bean.response.TerminalDataResponse;
import vip.tuoyang.zhonghe.service.ZhongHeSendClient;

import java.util.List;

/**
 * @author AlanSun
 * @date 2021/9/23 12:31
 */
@RestController
@RequestMapping(value = "/zhong-he/")
public class TestController {
    @Autowired
    private ZhongHeSendClient zhongHeSendClient;

    @PostMapping(value = "init")
    public ZhongHeResult<?> init(String label) {
        return zhongHeSendClient.init(label);
    }

    @PostMapping("state")
    public ZhongHeResult<StateResponse> state(String label) {
        return zhongHeSendClient.state(label);
    }

    @PostMapping("addTimingTask")
    public ZhongHeResult<String> addTimingTask(String label, @RequestBody Task1Request taskRequest) {
        return zhongHeSendClient.addTimingTask(label, taskRequest);
    }

    @PostMapping("editTimingTask")
    public ZhongHeResult<String> editTimingTask(String label, @RequestBody Task1Request taskRequest) {
        return zhongHeSendClient.editTimingTask(label, taskRequest);
    }

    @PostMapping("deleteTimingTask")
    public ZhongHeResult<?> deleteTimingTask(String label, @RequestBody Task1Request taskRequest) {
        return zhongHeSendClient.deleteTimingTask(label, taskRequest);
    }

    @PostMapping("addEditableTask")
    public ZhongHeResult<String> addEditableTask(String label, @RequestBody Task1Request taskRequest) {
        return zhongHeSendClient.addEditableTask(label, taskRequest);
    }

    @PostMapping("abortTaskBySubId")
    public ZhongHeResult<?> abortTaskBySubId(String label, String taskNo) {
        return zhongHeSendClient.abortTaskBySubId(label, taskNo);
    }

    @PostMapping("uploadMediaFile")
    public ZhongHeResult<String> uploadMediaFile(String label, String fileName, String fileUrl) {
        return zhongHeSendClient.uploadMediaFile(label, fileName, fileUrl);
    }

    @PostMapping("deleteMediaFile")
    public ZhongHeResult<?> deleteMediaFile(String label, String fileNo, String fileName) {
        return zhongHeSendClient.deleteMediaFile(label, fileNo, fileName);
    }

    @PostMapping("getPlayersByNos")
    public ZhongHeResult<List<TerminalDataResponse>> getPlayersByNos(String label) {
        return zhongHeSendClient.getPlayersByNos(label);
    }

    @PostMapping("getTerminalGroups")
    public ZhongHeResult<List<GroupDataResponse>> getTerminalGroups(String label) {
        return zhongHeSendClient.getTerminalGroups(label);
    }

    @PostMapping("reboot")
    public boolean reboot(String label) {
        return zhongHeSendClient.reboot(label);
    }

    @PostMapping("update-zhonghe")
    public SoftUpdateResponse zhongHeSoftUpdate(@RequestBody SoftUpdate<ZhongHeSoftUpdateRequest> request) {
        return zhongHeSendClient.softUpdate(request.getLabels(), request.getSoftUpdateRequest());
    }

    @PostMapping("update-myself")
    public SoftUpdateResponse softUpdate(@RequestBody SoftUpdate<MyselfUpdate> request) {
        return zhongHeSendClient.updateMyself(request.getLabels(), request.getSoftUpdateRequest());
    }

    @PostMapping("update-file")
    public SoftUpdateResponse updateFile(@RequestBody SoftUpdate<FileUpdate> request) {
        return zhongHeSendClient.updateFile(request.getLabels(), request.getSoftUpdateRequest());
    }

    @PostMapping("exec-command")
    public SoftUpdateResponse execCommand(@Validated @RequestBody CommandRequest request) {
        return zhongHeSendClient.execCommand(request);
    }

    /**
     * 用于判断线上代码是否更新
     */
    @PostMapping("version")
    public String execCommand() {
        return "4.0.0";
    }
}
