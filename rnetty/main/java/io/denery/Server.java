package io.denery;

import io.denery.packets.AuthPacket;
import io.denery.packets.VersionPacket;
import io.denery.packetutil.PacketBootstrap;
import io.denery.packetutil.utils.PacketUnsafeUtils;
import io.denery.util.TokenFormat;
import reactor.netty.DisposableServer;
import reactor.netty.tcp.TcpServer;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.*;

public class Server {
    //Denery's library for simple packaging of data, you don't need to learn this lib, it is created for simplicity
    // of visualising examples with networking, that's all.
    private static PacketUnsafeUtils utils = PacketUnsafeUtils.getInstance(PacketBootstrap.getInstance()
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
            throw new RuntimeException("Cannot start server!", e);
        }
        service.shutdown();
    }

    static class ServerLauncher {
        /**
         * method that starts server with given host and port and handles all data.
         *
         * @param host Server's port.
         * @param port Server's host.
         * @return Server's instance which you can stop and do other actions later.
         */
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
                            //Identifying packets by registered IDs and do actions with them.
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
