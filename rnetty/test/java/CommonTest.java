import io.denery.common.Packet;
import io.denery.common.PacketBootstrap;
import io.denery.common.PacketUtils;
import io.denery.packets.AuthPacket;
import io.denery.util.TokenFormat;
import org.junit.jupiter.api.Test;
import static org.junit.Assert.*;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class CommonTest {

    @Test
    void tokenFormatTest() {
            String nickname = "denery";
            String token = TokenFormat.generateToken("denery");
            String nick = TokenFormat.getNameByToken(token.getBytes(StandardCharsets.UTF_8));
            assertEquals(nickname, nick);
    }

    @Test
    void packetFormattingTest() {
        Packet samplePacket = new Packet((byte) 0x1);

        PacketUtils utils = PacketUtils.getInstance(PacketBootstrap.getInstance()
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
            assertEquals(samplePacket.id(), id);
        });
    }
}