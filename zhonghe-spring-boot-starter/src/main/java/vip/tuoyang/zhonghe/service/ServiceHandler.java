package vip.tuoyang.zhonghe.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.ReadTimeoutException;
import lombok.extern.slf4j.Slf4j;
import vip.tuoyang.base.constants.SeparatorConstants;
import vip.tuoyang.base.exception.BizException;
import vip.tuoyang.zhonghe.bean.ZhongHeDto;
import vip.tuoyang.zhonghe.bean.ZhongHeResult;
import vip.tuoyang.zhonghe.bean.request.StateRequest;
import vip.tuoyang.zhonghe.bean.response.GroupDataResponse;
import vip.tuoyang.zhonghe.bean.response.StateResponse;
import vip.tuoyang.zhonghe.bean.response.TerminalDataResponse;
import vip.tuoyang.zhonghe.config.ObjectMapperConfig;
import vip.tuoyang.zhonghe.config.ZhongHeConfig;
import vip.tuoyang.zhonghe.support.ServiceZhongHeCallback;
import vip.tuoyang.zhonghe.support.SyncSupport;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author AlanSun
 * @date 2021/9/22 13:58
 */
@Slf4j
@ChannelHandler.Sharable
public class ServiceHandler extends SimpleChannelInboundHandler<String> {

    public static final Map<String, ChannelHandlerContext> LABEL_CHANNEL_MAP = new ConcurrentHashMap<>(64);
    public static final Map<ChannelHandlerContext, String> CHANNEL_LABEL_MAP = new ConcurrentHashMap<>(64);

    private final ObjectMapper objectMapper;

    private final ServiceZhongHeCallback serviceZhongHeCallback;

    public ServiceHandler(ServiceZhongHeCallback serviceZhongHeCallback) {
        this.objectMapper = ObjectMapperConfig.getObjectMapper();
        this.serviceZhongHeCallback = serviceZhongHeCallback;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        final int commandStartIndex = msg.indexOf("command") + 9;
        final int firstCommaIndex = msg.substring(commandStartIndex).indexOf(SeparatorConstants.COMMA);
        int commandEndIndex;
        if (firstCommaIndex != -1) {
            commandEndIndex = commandStartIndex + firstCommaIndex;
        } else {
            commandEndIndex = msg.length() - 1;
        }
        byte command = Byte.parseByte(msg.substring(commandStartIndex, commandEndIndex));
        try {
            switch (command) {
                case 1:
                case 5:
                case 7:
                case 9:
                case 12:
                case 15:
                case 16:
                case 17:
                case 18:
                    ZhongHeDto<ZhongHeResult<Object>> zhongHeResult = objectMapper.readValue(msg, new TypeReference<ZhongHeDto<ZhongHeResult<Object>>>() {
                    });
                    this.resultHandle(zhongHeResult);
                    break;
                case 2:
                    final ZhongHeDto<ZhongHeResult<StateResponse>> zhongHeResult2 = objectMapper.readValue(msg, new TypeReference<ZhongHeDto<ZhongHeResult<StateResponse>>>() {
                    });
                    this.resultHandle(zhongHeResult2);
                    break;
                case 3:
                case 4:
                case 6:
                case 8:
                    final ZhongHeDto<ZhongHeResult<String>> zhongHeResult3 = objectMapper.readValue(msg, new TypeReference<ZhongHeDto<ZhongHeResult<String>>>() {
                    });
                    this.resultHandle(zhongHeResult3);
                    break;
                case 10:
                    final ZhongHeDto<ZhongHeResult<List<TerminalDataResponse>>> zhongHeResult10 = objectMapper.readValue(msg, new TypeReference<ZhongHeDto<ZhongHeResult<List<TerminalDataResponse>>>>() {
                    });
                    this.resultHandle(zhongHeResult10);
                    break;
                case 11:
                    final ZhongHeDto<ZhongHeResult<List<GroupDataResponse>>> zhongHeResult11 = objectMapper.readValue(msg, new TypeReference<ZhongHeDto<ZhongHeResult<List<GroupDataResponse>>>>() {
                    });
                    this.resultHandle(zhongHeResult11);
                    break;
                case 13:
                    ZhongHeDto<StateRequest> requestZhongHeBaseRequest13 = objectMapper.readValue(msg, new TypeReference<ZhongHeDto<StateRequest>>() {
                    });
                    serviceZhongHeCallback.stateChange(requestZhongHeBaseRequest13.getData());
                    break;
                case 14:
                    ZhongHeDto<ZhongHeConfig> requestZhongHeBaseRequest14 = objectMapper.readValue(msg, new TypeReference<ZhongHeDto<ZhongHeConfig>>() {
                    });
                    final ChannelHandlerContext oldCtx = LABEL_CHANNEL_MAP.get(requestZhongHeBaseRequest14.getLabel());
                    if (oldCtx != null) {
                        log.info("????????????????????????, label: [{}]", requestZhongHeBaseRequest14.getLabel());
                        // ??????????????????????????????????????????
                        CHANNEL_LABEL_MAP.remove(oldCtx);
                        oldCtx.close();
                    }
                    LABEL_CHANNEL_MAP.put(requestZhongHeBaseRequest14.getLabel(), ctx);
                    CHANNEL_LABEL_MAP.put(ctx, requestZhongHeBaseRequest14.getLabel());
                    if (requestZhongHeBaseRequest14.getData() != null) {
                        serviceZhongHeCallback.serverInit(requestZhongHeBaseRequest14.getData());
                    } else {
                        // ???????????????????????????
                        StateRequest stateRequest = new StateRequest();
                        stateRequest.setLabel(requestZhongHeBaseRequest14.getLabel());
                        stateRequest.setState(1);
                        log.info("?????????????????????, param: [{}]", stateRequest);
                        serviceZhongHeCallback.stateChange(stateRequest);
                    }
                    break;
                default:
                    throw new UnsupportedOperationException("??????????????????: msg: " + msg);
            }
        } catch (Throwable t) {
            log.error("error", t);
            if (t instanceof BizException) {
                ZhongHeDto<ZhongHeResult<Object>> zhongHeResult = new ZhongHeDto<>();
                ZhongHeResult<Object> objectZhongHeResult = new ZhongHeResult<>();
                objectZhongHeResult.setSuccess(false);
                objectZhongHeResult.setErrorMsg(t.getMessage());
                zhongHeResult.setData(objectZhongHeResult);
                this.resultHandle(zhongHeResult);
            } else {
                throw t;
            }
        }
    }

    private <T> void resultHandle(ZhongHeDto<ZhongHeResult<T>> zhongHeResult) {
        if (zhongHeResult != null) {
            String label = zhongHeResult.getLabel();
            if (label != null) {
                SyncSupport.labelResultMap.put(label, zhongHeResult.getData());
                SyncSupport.getCountDownLatch2(label).countDown();
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (cause instanceof ReadTimeoutException) {
            log.error("???????????????????????????", cause);
        } else {
            log.error("???????????????????????????", cause);
        }

        ctx.close();

        final boolean b = CHANNEL_LABEL_MAP.containsKey(ctx);
        if (b) {
            // ????????????????????????????????????
            StateRequest stateRequest = new StateRequest();
            stateRequest.setLabel(CHANNEL_LABEL_MAP.get(ctx));
            stateRequest.setState(0);
            log.info("???????????????????????????, param: [{}]", stateRequest);
            serviceZhongHeCallback.stateChange(stateRequest);
        } else {
            log.info("????????????,?????????????????????,???????????????");
        }
    }
}
