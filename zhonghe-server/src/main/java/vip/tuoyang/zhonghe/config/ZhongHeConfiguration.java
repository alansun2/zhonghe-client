package vip.tuoyang.zhonghe.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;
import vip.tuoyang.base.codec.jackson.JacksonConfig;
import vip.tuoyang.zhonghe.bean.ZhongHeDto;
import vip.tuoyang.zhonghe.config.properties.ServiceSystemProperties;
import vip.tuoyang.zhonghe.nettyclient.BroadcastClient;
import vip.tuoyang.zhonghe.service.ServiceHandler;
import vip.tuoyang.zhonghe.nettyclient.codec.ZhongHeDtoEncoder;
import vip.tuoyang.zhonghe.service.ZhongHeClient;
import vip.tuoyang.zhonghe.service.ZhongHeClientImpl;
import vip.tuoyang.zhonghe.support.MyCallbackHandler;
import vip.tuoyang.zhonghe.support.MyServiceCallback;
import vip.tuoyang.zhonghe.support.ZhongHeCallback;
import vip.tuoyang.zhonghe.support.ZhongHeClientLockProxy;

import javax.annotation.PostConstruct;

/**
 * @author AlanSun
 * @date 2021/9/3 14:22
 */
@Import(JacksonConfig.class)
@Order
@Configuration
public class ZhongHeConfiguration {
    @Autowired
    private ServiceSystemProperties serviceSystemProperties;

    @PostConstruct
    public void init() {
        ZhongHeSystemProperties.timeout = serviceSystemProperties.getTimeout();
    }

    @Bean
    public ServiceHandler broadcastHandler() {
        return new ServiceHandler();
    }

    @Bean
    public ZhongHeCallback zhongHeCallback() {
        return new MyCallbackHandler();
    }

    @Bean
    public ZhongHeDtoEncoder zhongHeDtoEncoder() {
        return new ZhongHeDtoEncoder();
    }

    @Bean
    public ZhongHeClient zhongHeClient() {
        return ZhongHeClientLockProxy.getProxy(ZhongHeClientImpl.create(serviceSystemProperties.getZhongHeConfig(), "label", zhongHeCallback()), "label");
    }

    @Bean
    public BroadcastClient broadcastClient() {
        BroadcastClient broadcastClient = new BroadcastClient(this.broadcastHandler(), zhongHeDtoEncoder(), new MyServiceCallback(), serviceSystemProperties.getTcpPort(), serviceSystemProperties.getTcpHost());
        broadcastClient.connect();
        broadcastClient.sendMessage("token:" + serviceSystemProperties.getSecret());
        ZhongHeDto<ZhongHeConfig> zhongHeBaseRequest = new ZhongHeDto<>();
        zhongHeBaseRequest.setData(serviceSystemProperties.getZhongHeConfig());
        zhongHeBaseRequest.setCommand((byte) 14);
        broadcastClient.sendMessage(zhongHeBaseRequest);
        return broadcastClient;
    }
}
