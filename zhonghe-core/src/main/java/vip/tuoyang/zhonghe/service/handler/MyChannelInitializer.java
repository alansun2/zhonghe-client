
package vip.tuoyang.zhonghe.service.handler;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.nio.NioDatagramChannel;
import vip.tuoyang.zhonghe.service.SendClient;
import vip.tuoyang.zhonghe.support.StateCallback;

/**
 * @author AlanSun
 * @date 2021/8/22 13:27
 **/
public class MyChannelInitializer extends ChannelInitializer<NioDatagramChannel> {

    private final String label;

    private final StateCallback stateCallback;

    private final SendClient sendClient;

    public MyChannelInitializer(String label, StateCallback stateCallback, SendClient sendClient) {
        this.label = label;
        this.stateCallback = stateCallback;
        this.sendClient = sendClient;
    }

    @Override
    protected void initChannel(NioDatagramChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();
        // 解码转String，注意调整自己的编码格式GBK、UTF-8
        //pipeline.addLast("stringDecoder", new StringDecoder(Charset.forName("GBK")));
//        pipeline.addLast(new IdleStateHandler(0, 30, 0));
        pipeline.addLast(new MyClientHandler(label, stateCallback, sendClient));
    }
}