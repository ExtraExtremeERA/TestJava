package io.denery;

import io.denery.common.BNPacket;
import io.denery.common.BNPackets;
import io.denery.common.util.TokenFormat;
import reactor.netty.DisposableServer;
import reactor.netty.tcp.TcpServer;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.*;

public class Server {
    public static void main(String[] args) {
        ServerLauncher server = new ServerLauncher();
        //Running server in another thread using Executor Service.
        ExecutorService service = Executors.newSingleThreadExecutor();
        Future<?> future = service.submit(() -> {
            server.launch("127.0.0.1", 40504).onDispose().block();
        });
        try {
            future.get();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Cannot execute server!", e);
        }
        service.shutdown();
    }

    static class ServerLauncher {
        public DisposableServer launch(String host, int port) {
            System.out.println("Starting server on Thread: " + Thread.currentThread().getName());
            return TcpServer.create()
                    .host(host)
                    .port(port)
                    //Receiving solid stream of bytes from client
                    .handle((nettyInbound, nettyOutbound) -> nettyInbound.receive().asByteArray().handle((packetss, sink) -> {
                        System.out.println("handling packets.");
                        //Parsing this solid stream of bytes to the sequence of Packet objects.
                        BNPacket.parsePacketStream(packetss).subscribe(packet -> {
                            System.out.println("handling each packet.");
                            //Identifying packets and do actions with them.
                            if (packet.id() == BNPackets.AUTH.getId()) {
                                System.out.println(TokenFormat.getNameByToken(packet.parsePacket()));
                            }

                            if (packet.id() == BNPackets.VERSION.getId()) {
                                System.out.println(new String(packet.parsePacket(), StandardCharsets.UTF_8));
                            }
                        });
                        sink.next(packetss);
                    }).then())
                    .bindNow();
        }
    }
}
