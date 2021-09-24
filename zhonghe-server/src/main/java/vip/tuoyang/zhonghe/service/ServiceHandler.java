package vip.tuoyang.zhonghe.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import vip.tuoyang.base.constants.SeparatorConstants;
import vip.tuoyang.base.util.AssertUtils;
import vip.tuoyang.zhonghe.bean.ZhongHeDto;
import vip.tuoyang.zhonghe.bean.ZhongHeResult;
import vip.tuoyang.zhonghe.bean.request.FileUploadRequest;
import vip.tuoyang.zhonghe.bean.request.Task1Request;
import vip.tuoyang.zhonghe.bean.request.TaskRequest;
import vip.tuoyang.zhonghe.config.properties.ServiceSystemProperties;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Base64;

/**
 * @author AlanSun
 * @date 2021/9/22 17:32
 */
@ChannelHandler.Sharable
@Slf4j
public class ServiceHandler extends SimpleChannelInboundHandler<String> {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ZhongHeClient zhongHeClient;
    @Autowired
    private CommonService commonService;
    @Autowired
    private ServiceSystemProperties serviceSystemProperties;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) {
        final int commandStartIndex = msg.indexOf("command") + 9;
        final int firstCommaIndex = msg.substring(commandStartIndex).indexOf(SeparatorConstants.COMMA);
        int commandEndIndex;
        if (firstCommaIndex != -1) {
            commandEndIndex = commandStartIndex + firstCommaIndex;
        } else {
            commandEndIndex = msg.length() - 1;
        }
        byte command = Byte.parseByte(msg.substring(commandStartIndex, commandEndIndex));
        ZhongHeResult<?> zhongHeResult;
        try {
            switch (command) {
                case 1:
                    final ZhongHeDto<Boolean> zhongHeBaseRequest1 = objectMapper.readValue(msg, new TypeReference<ZhongHeDto<Boolean>>() {
                    });
                    zhongHeResult = zhongHeClient.initMiddleWare(zhongHeBaseRequest1.getData());
                    break;
                case 2:
                    zhongHeResult = zhongHeClient.state();
                    break;
                case 3:
                    final ZhongHeDto<TaskRequest> zhongHeBaseRequest3 = objectMapper.readValue(msg, new TypeReference<ZhongHeDto<TaskRequest>>() {
                    });
                    zhongHeResult = zhongHeClient.addTimingTask(zhongHeBaseRequest3.getData());
                    break;
                case 4:
                    final ZhongHeDto<Task1Request> zhongHeBaseRequest4 = objectMapper.readValue(msg, new TypeReference<ZhongHeDto<Task1Request>>() {
                    });
                    zhongHeResult = zhongHeClient.editTimingTask(zhongHeBaseRequest4.getData().getTaskNo(), zhongHeBaseRequest4.getData().getTaskRequest());
                    break;
                case 5:
                    final ZhongHeDto<Task1Request> zhongHeBaseRequest5 = objectMapper.readValue(msg, new TypeReference<ZhongHeDto<Task1Request>>() {
                    });
                    zhongHeResult = zhongHeClient.deleteTimingTask(zhongHeBaseRequest5.getData().getTaskNo(), zhongHeBaseRequest5.getData().getTaskRequest());
                    break;
                case 6:
                    final ZhongHeDto<TaskRequest> zhongHeBaseRequest6 = objectMapper.readValue(msg, new TypeReference<ZhongHeDto<TaskRequest>>() {
                    });
                    zhongHeResult = zhongHeClient.addEditableTask(zhongHeBaseRequest6.getData());
                    break;
                case 7:
                    final ZhongHeDto<String> zhongHeBaseRequest7 = objectMapper.readValue(msg, new TypeReference<ZhongHeDto<String>>() {
                    });
                    zhongHeResult = zhongHeClient.abortTaskBySubId(zhongHeBaseRequest7.getData());
                    break;
                case 8:
                    final ZhongHeDto<FileUploadRequest> zhongHeBaseRequest8 = objectMapper.readValue(msg, new TypeReference<ZhongHeDto<FileUploadRequest>>() {
                    });
                    final FileUploadRequest fileUploadRequest8 = zhongHeBaseRequest8.getData();
                    AssertUtils.notNull(fileUploadRequest8, "文件body不能为空");
                    final String fileName = fileUploadRequest8.getFileName();
                    AssertUtils.notNull(fileName, "文件名称不能为空");
                    final String contentBase64 = fileUploadRequest8.getContentBase64();
                    AssertUtils.notNull(fileUploadRequest8, "文件内容不能为空");

                    String filePath = serviceSystemProperties.getFileDir() + fileName;
                    final byte[] decode = Base64.getDecoder().decode(contentBase64);
                    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(decode);
                    FileUtils.copyInputStreamToFile(byteArrayInputStream, new File(filePath));
                    zhongHeResult = zhongHeClient.uploadMediaFile(filePath);
                    break;
                case 9:
                    final ZhongHeDto<FileUploadRequest> zhongHeBaseRequest9 = objectMapper.readValue(msg, new TypeReference<ZhongHeDto<FileUploadRequest>>() {
                    });
                    final FileUploadRequest fileUploadRequest = zhongHeBaseRequest9.getData();
                    zhongHeResult = zhongHeClient.deleteMediaFile(fileUploadRequest.getFileNo(), fileUploadRequest.getFileName());
                    break;
                case 10:
                    zhongHeResult = zhongHeClient.getPlayersByNos();
                    break;
                case 11:
                    zhongHeResult = zhongHeClient.getTerminalGroups();
                    break;
                case 12:
                    zhongHeResult = new ZhongHeResult<>();
                    commonService.reboot();
                    break;
                default:
                    log.error("指令不存在, msg: [{}]", msg);
                    return;
            }
        } catch (Throwable t) {
            zhongHeResult = new ZhongHeResult<>();
            zhongHeResult.setSuccess(false);
            zhongHeResult.setErrorMsg(StringUtils.substring(t.getMessage(), 0, 400));
        }

        ZhongHeDto<ZhongHeResult> zhongHeDto = new ZhongHeDto<>();
        zhongHeDto.setData(zhongHeResult);
        zhongHeDto.setCommand(command);
        ctx.writeAndFlush(zhongHeDto);
    }
}