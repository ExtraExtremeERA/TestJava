package io.denery;

import io.denery.common.PacketBootstrap;
import io.denery.common.PacketUtils;
import io.denery.packets.ByteCodePacket;
import org.threadly.concurrent.PriorityScheduler;
import org.threadly.concurrent.TaskPriority;
import reactor.netty.DisposableServer;
import reactor.netty.tcp.TcpServer;

public class Server {
    private final PacketUtils packetUtils = PacketUtils.getInstance(PacketBootstrap.getInstance()
            .newPacketBuilder()
                    .registerPacket(new ByteCodePacket())
            .build()
    );

    public static void main(String[] args) {
        int tts = Runtime.getRuntime().availableProcessors() * 2;
        PriorityScheduler ps = new PriorityScheduler(1);
        ps.submit(() -> {
            new ServerLauncher().launch("127.0.0.1", 35575).onDispose().block();
        }, TaskPriority.High);
        ps.shutdown();
    }

    public static class ServerLauncher {
        public DisposableServer launch(String host, int port) {
            return TcpServer.create()
                    .host(host)
                    .port(port)
                    .handle((nettyInbound, nettyOutbound) -> nettyInbound.receive().asByteArray().handle((packetss, sink) -> {

                    }).then())
                    .bindNow();
        }
    }
}
