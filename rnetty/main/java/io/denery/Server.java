package io.denery;

import io.denery.common.PacketBootstrap;
import io.denery.common.PacketUtils;
import io.denery.util.TokenFormat;
import io.denery.packets.AuthPacket;
import io.denery.packets.VersionPacket;
import reactor.netty.DisposableServer;
import reactor.netty.tcp.TcpServer;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.*;

public class Server {
    private static PacketUtils utils = PacketUtils.getInstance(PacketBootstrap.getInstance()
            .newPacketBuilder()
            .registerPacket(new AuthPacket())
            .registerPacket(new VersionPacket())
            .build());

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
                        utils.parsePacketStream(packetss).subscribe(packet -> {
                            System.out.println("handling each packet.");
                            //Identifying packets and do actions with them.
                            if (packet.id() == 0x1) {
                                System.out.println(TokenFormat.getNameByToken(packet.parsePacket()));
                            }

                            if (packet.id() == 0x2) {
                                System.out.println(new String(packet.parsePacket(), StandardCharsets.UTF_8));
                            }
                        });
                        sink.next(packetss);
                    }).then())
                    .bindNow();
        }
    }
}
