package io.denery;

import io.denery.common.BNPacket;
import io.denery.common.BNPackets;
import io.denery.common.util.TokenFormat;
import reactor.core.publisher.Flux;
import reactor.netty.Connection;
import reactor.netty.tcp.TcpClient;

import java.util.Arrays;

public class Client {
    public static void main(String[] args) {
        Connection connection = TcpClient.create()
                .host("0.0.0.0")
                .port(49200)
                .connectNow();

        connection.inbound()
                .receive()
                .asByteArray()
                .subscribe(s -> {
                    Flux<BNPacket> cflux = BNPacket.parsePacketStream(s);

                    cflux.subscribe(packet -> {
                        if (packet.id() == BNPackets.AUTH.getId()) {
                            String name = TokenFormat.getNameByToken(packet.parsePacket());
                            System.out.println(name);
                        } else {
                            System.out.println(Arrays.toString(packet.parsePacket()));
                        }
                    });
                });

        connection.onDispose().block();
    }


}
