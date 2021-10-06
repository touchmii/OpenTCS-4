package com.lvsrobot.vehicletcp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.DisposableServer;
import reactor.netty.NettyInbound;
import reactor.netty.NettyOutbound;
import reactor.netty.tcp.TcpServer;

public class TCPServer {

    private static final Logger LOG = LoggerFactory.getLogger(TCPServer.class);
    private TcpServer tcpServer;
    public TCPServer() {
//        tcpServer.handle()
        tcpServer = TcpServer.create().handle((inbound, outbound) ->
            inbound.receive().asString()
            .flatMap(string -> {
            LOG.info("recive host: {} msg: {}", "xx", string);
            return Mono.empty();
    })
        );
        tcpServer.warmup().block();
        DisposableServer server = tcpServer.host("0.0.0.0").port(44444).bindNow();
        server.onDispose().block();
    }

//    private BiFunction<? super NettyInbound,? super NettyOutbound,? extends Publisher<Void>> ServerHandler() {
//    }

    public Mono<Void> ServerHandler(NettyInbound inbound, NettyOutbound outbound) {
         Flux<String> rec = inbound.receive().asString().flatMap(
                 string -> {
                     LOG.info("recive host: {} msg: {}", "xx", string);
                     return Mono.empty();
                 }
         );

         return Mono.empty();
    }
}
