package io.denery;

import io.denery.common.PacketBootstrap;
import io.denery.common.PacketUtils;
import io.denery.util.TokenFormat;
import io.denery.packets.AuthPacket;
import io.denery.packets.VersionPacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import reactor.core.publisher.Flux;;
import reactor.netty.Connection;
import reactor.netty.tcp.TcpClient;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Client {
    private static final PacketUtils utils = PacketUtils.getInstance(PacketBootstrap.getInstance()
            .newPacketBuilder()
            .registerPacket(new AuthPacket())
            .registerPacket(new VersionPacket())
            .build());

    public static void main(String[] args) {
        ClientLauncher launcher = new ClientLauncher();

        //Running client in another thread using Executor Service.
        ExecutorService service = Executors.newSingleThreadExecutor();
        Future<?> future = service.submit(() -> {
            launcher.launch("127.0.0.1", 40504).dispose();
        });
        try {
            future.get();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Cannot execute client!", e);
        }
        service.shutdown();
    }


    static class ClientLauncher {
        public Connection launch(String host, int port) {
            System.out.println("Starting client on thread: " + Thread.currentThread().getName());
            Connection connection = TcpClient.create()
                    .host(host)
                    .port(port)
                    .connectNow();

            Flux<ByteBuf> clientInfo = Flux.create(s -> {
                //Generating a sequence of 50 random numbers every second as Auth packet and then converting it all to ByteBuf.
                for (int i = 0; i != 50; i++) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    String name = Double.toString(Math.random());
                    s.next(Unpooled.wrappedBuffer(utils.formatPacket((byte) 0x1, TokenFormat.generateToken(name)
                            .getBytes(StandardCharsets.UTF_8)).getData()));
                }
            });

            connection.inbound()
                    .receive()
                    .then();

            //Sending sequence.
            connection.outbound()
                    .send(clientInfo)
                    .then()
                    .subscribe();

            return connection;
        }
    }

}
