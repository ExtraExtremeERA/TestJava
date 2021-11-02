package io.denery;

import io.denery.packetutil.Packet;
import io.denery.packetutil.PacketBootstrap;
import io.denery.packetutil.utils.PacketUnsafeUtils;
import io.denery.packetutil.utils.PacketUtils;
import io.denery.util.TokenFormat;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

class CommonTest {

    @Test
    void tokenFormatTest() {
            String nickname = "denery";
            String token = TokenFormat.generateToken("denery");
            String nick = TokenFormat.getNameByToken(token.getBytes(StandardCharsets.UTF_8));
            assertEquals(nickname, nick);
    }

    @Test
    void packetFormattingTestUsual() {
        Packet samplePacket = new Packet((byte) 0x1);
        PacketUtils utils = PacketUtils.getInstance(PacketBootstrap.getInstance()
                .newPacketBuilder()
                        .registerPacket(samplePacket)
                .build()
        );
        final byte[] data = TokenFormat.generateToken("denery").getBytes(StandardCharsets.UTF_8);
        Packet p = utils.formatPacket(samplePacket.id(), data);
        System.out.println(Arrays.toString(p.getData()));
    }

    @Test
    void packetFormattingTestUnsafe() {
        Packet samplePacket = new Packet((byte) 0x1);

        PacketUnsafeUtils utils = PacketUnsafeUtils.getInstance(PacketBootstrap.getInstance()
                .newPacketBuilder()
                        .registerPacket(samplePacket)
                .build()
        );

        final byte[] data = TokenFormat.generateToken("denery").getBytes(StandardCharsets.UTF_8);
        List<Byte> list = new ArrayList<>();
        byte[] packetBytes = utils.formatPacket(samplePacket.id(), data).getData();
        for (byte b : packetBytes) {
            list.add(b);
        }
        for (int i = 0; i < 2; i++) {
            list.addAll(list);
        }
        byte[] packetBytesSolidStream = new byte[list.size()];
        int a = 0;
        for (Byte b : list) {
            packetBytesSolidStream[a] = b;
            a++;
        }
        utils.parsePacketStream(packetBytesSolidStream).subscribe(s -> {
            byte[] data1 = s.parsePacket();
            byte id = s.id();
            assertArrayEquals(data, data1);
            System.out.println(Arrays.toString(data));
            assertEquals(samplePacket.id(), id);
        });
    }
}