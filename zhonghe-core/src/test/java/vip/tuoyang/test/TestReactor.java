package vip.tuoyang.test;

import io.netty.channel.Channel;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.netty.Connection;
import reactor.netty.DisposableServer;
import reactor.netty.tcp.TcpClient;
import reactor.netty.tcp.TcpServer;
import vip.tuoyang.zhonghe.config.ZhongHeConfig;
import vip.tuoyang.zhonghe.service.SendClient;

/**
 * @author AlanSun
 * @date 2021/8/28 10:41
 */
public class TestReactor {
    @Test
    public void udpClient() {
        Connection connection =
                TcpClient.create()
                        .host("localhost")
                        .port(80)
                        .handle((inbound, outbound) -> {
                            outbound.sendString(Mono.just("hello"));
                            return outbound.sendString(Mono.just("hello"));
                        })
                        .connectNow();

        connection.outbound().sendString(Mono.just("sdfasdf"));
        connection.onDispose().block();
    }

    @Test
    public void startServer() {
        DisposableServer server =
                TcpServer.create()
                        .host("localhost")
                        .port(80)
                        .handle((inbound, outbound) -> {
                            return inbound.receive().asString().log().then();
                        })
                        .bindNow();

        server.onDispose()
                .block();
    }

    @Test
    public void testAddressBind() {
        ZhongHeConfig zhongHeConfig = new ZhongHeConfig();
        String localIp = "192.168.166.151";
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
        zhongHeConfig.setFileUploadUrl("http://localhost:8084/common/upload-file");
        SendClient sendClient = new SendClient(zhongHeConfig, "test", (label, stateHandler) -> {
        });

        final Channel channel = sendClient.getChannel();
        sendClient.close();
        sendClient = new SendClient(zhongHeConfig, "test", (label, stateHandler) -> {
        });
        final Channel channel1 = sendClient.getChannel();
    }
}
