package vip.tuoyang.test;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.netty.Connection;
import reactor.netty.DisposableServer;
import reactor.netty.tcp.TcpClient;
import reactor.netty.tcp.TcpServer;

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
}
