package io.denery;

import io.denery.common.BNPacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import reactor.core.publisher.Flux;
import reactor.netty.DisposableServer;
import reactor.netty.tcp.TcpServer;

public class Server {
    public void run(String host, int port, Flux<BNPacket> packets) {

        DisposableServer server = TcpServer.create()
                .host(host)
                .port(port)
                .handle((nettyInbound, nettyOutbound) -> {
                    Flux<ByteBuf> byteFlux = packets.handle((packet, sink) -> {
                        sink.next(Unpooled.wrappedBuffer(packet.getData()));
                    });

                    return nettyOutbound.send(byteFlux);
                }).bindNow();

        server.onDispose().block();
    }
}
