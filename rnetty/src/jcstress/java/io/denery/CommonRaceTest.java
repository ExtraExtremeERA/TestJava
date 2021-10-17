package io.denery;

import io.denery.common.Packet;
import io.denery.common.UtilPackets;
import org.openjdk.jcstress.annotations.*;
import org.openjdk.jcstress.infra.results.I_Result;
import reactor.core.publisher.Flux;

import java.util.Arrays;

@JCStressTest
//@Outcome(id = "1", expect = Expect.ACCEPTABLE, desc = "Normal state.")
//@Outcome(id = "35", expect = Expect.ACCEPTABLE_INTERESTING, desc = "ID lost.")
@Outcome(expect = Expect.ACCEPTABLE_INTERESTING, desc = "any other")
@State
//Another Temporary class that will be deleted/modified soon.
public class CommonRaceTest {
    Flux<Packet> packets;
    public byte[] data = {49, 48, 48, 35, 49, 48, 49, 35, 49, 49, 48, 35, 49, 48, 49, 35, 49, 49, 52, 35, 49, 50, 49, 35, 1,
            49, 48, 48, 35, 49, 48, 49, 35, 49, 49, 48, 35, 49, 48, 49, 35, 49, 49, 52, 35, 49, 50, 49, 35, 1,
            49, 48, 48, 35, 49, 48, 49, 35, 49, 49, 48, 35, 49, 48, 49, 35, 49, 49, 52, 35, 49, 50, 49, 35, 1,
            49, 48, 48, 35, 49, 48, 49, 35, 49, 49, 48, 35, 49, 48, 49, 35, 49, 49, 52, 35, 49, 50, 49, 35, 1};
    byte[] rawData;
    byte[] formattedData;

    @Actor
    public void iteratingRawData() {
        packets = Flux.create(s -> {
            int i = 0;
            byte[] rawData = new byte[data.length];
            for (byte b : data) {
                rawData[i] = data[i];
                i++;

                if (isIDRegistered(b)) {
                    int a = 0;
                    for (byte b1 : rawData) {
                        if (b1 != 0) break;
                        a++;
                    }
                    int c = 0;
                    for (byte b1 : rawData) {
                        if (b1 == 0) c++;
                    }
                    byte[] formattedData = new byte[rawData.length - c];
                    int z = 0;
                    for (byte b1 : rawData) {
                        if (!(b1 == 0)) {
                            formattedData[z] = rawData[z + a];
                            z++;
                        }
                    }
                    s.next(identifyPacket(formattedData));
                    rawData = new byte[data.length];
                }
            }
        });
    }

    public Packet identifyPacket(byte[] data) {
        if (isIDRegistered(data[data.length - 1])) {
            return new Packet(data[data.length - 1], data) {};
        } else {
            return new UtilPackets.EmptyPacket();
        }
    }

    @Arbiter
    public void arbiter(I_Result r) {
        packets.subscribe(s -> {
            r.r1 = s.id();
        });
    }

    public boolean isIDRegistered(byte id) {
        return id == 0x1;
    }

}
