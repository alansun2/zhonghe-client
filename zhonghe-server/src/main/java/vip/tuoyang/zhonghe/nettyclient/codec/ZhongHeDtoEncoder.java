package vip.tuoyang.zhonghe.nettyclient.codec;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import vip.tuoyang.zhonghe.bean.ZhongHeDto;
import vip.tuoyang.zhonghe.config.properties.ServiceSystemProperties;

import java.util.List;

/**
 * @author AlanSun
 * @date 2021/9/23 16:03
 */
@ChannelHandler.Sharable
public class ZhongHeDtoEncoder extends MessageToMessageEncoder<ZhongHeDto> {
    @Autowired
    private ServiceSystemProperties serviceSystemProperties;
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    protected void encode(ChannelHandlerContext ctx, ZhongHeDto msg, List<Object> out) throws Exception {
        msg.setLabel(serviceSystemProperties.getZhongHeConfig().getLabel());
        out.add(objectMapper.writeValueAsString(msg));
    }
}
