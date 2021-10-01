package io.denery;

import io.denery.common.BNPacket;
import io.denery.common.BNPackets;
import io.denery.common.util.TokenFormat;
import reactor.core.publisher.Flux;

import java.nio.charset.StandardCharsets;

public class Main {
    public static void main(String[] args) {
        Server server = new Server();

        byte[] denery = TokenFormat.generateToken("Denery").getBytes(StandardCharsets.UTF_8);
        byte[] deelTer = TokenFormat.generateToken("DeelTer").getBytes(StandardCharsets.UTF_8);
        byte[] version = "0.1".getBytes(StandardCharsets.UTF_8);

        Flux<BNPacket> packetMono = Flux.create(sink -> {
            sink.next(BNPacket.formatPacket(denery, BNPackets.AUTH));
            sink.next(BNPacket.formatPacket(deelTer, BNPackets.AUTH));
            sink.next(BNPacket.formatPacket(version, BNPackets.VERSION));
        });

        server.run("0.0.0.0", 49200, packetMono);
    }
}
