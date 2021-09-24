package vip.tuoyang.zhonghe.nettyserver.codec;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import vip.tuoyang.zhonghe.bean.ZhongHeDto;
import vip.tuoyang.zhonghe.config.ObjectMapperConfig;

import java.util.List;

/**
 * @author AlanSun
 * @date 2021/9/24 9:08
 */
public class ZhongHeDtoEncoder extends MessageToMessageEncoder<ZhongHeDto> {

    private final ObjectMapper objectMapper;

    public ZhongHeDtoEncoder() {
        this.objectMapper = ObjectMapperConfig.getObjectMapper();
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, ZhongHeDto msg, List<Object> out) throws Exception {
        out.add(objectMapper.writeValueAsString(msg));
    }
}
