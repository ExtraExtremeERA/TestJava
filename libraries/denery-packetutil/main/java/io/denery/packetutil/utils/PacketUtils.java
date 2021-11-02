package io.denery.packetutil.utils;

import io.denery.packetutil.Packet;
import io.denery.packetutil.PacketBootstrap;
import io.denery.packetutil.UtilPackets;
import reactor.core.publisher.Flux;

/**
 * Will be developed later... It is not needed yet.
 */
public class PacketUtils extends AbstractPacketUtils {
    private final PacketBootstrap bootstrap;

    private PacketUtils(PacketBootstrap bootstrap) {
        this.bootstrap = bootstrap;
    }

    private static PacketUtils instance;

    public static PacketUtils getInstance(PacketBootstrap bootstrap) {
        if (instance == null) {
            instance = new PacketUtils(bootstrap);
        }
        return instance;
    }

    public static PacketUtils getInstance() {
        if (instance == null) {
            instance = new PacketUtils(PacketBootstrap.getInstance());
        }
        return instance;
    }

    @Override
    public Flux<Packet> parsePacketStream(byte[] data) {
        return Flux.create(s -> {
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

    @Override
    public Packet formatPacket(byte id, byte[] data) {
        boolean isMultiplies127 = false;
        int tmp_127 = data.length;
        while (tmp_127 > 0) {
            tmp_127 = tmp_127 - 127;
        }
        if (tmp_127 == 0) isMultiplies127 = true;
        byte[] formattedData = new byte[data.length + 3 + data.length / 127];
        formattedData[0] = id;
        formattedData[1] = 0;
        if (isMultiplies127) formattedData[1] = 1;
        int tmp_adding = data.length;
        int tmp_adding1 = 2;
        while (tmp_adding > 0) {
            tmp_adding = tmp_adding - 127;
            formattedData[tmp_adding1] = 127;
            tmp_adding1++;
        }
        System.arraycopy(data, 0, formattedData, 2 + tmp_adding1 + 1, data.length);
        return identifyPacket(formattedData);
    }

    @Override
    public Packet identifyPacket(byte[] data) {
        boolean contains = false;
        byte id = data[0];

        for(int i = 0; i < bootstrap.packetIDs.length; i++) {
            if (bootstrap.packetIDs[i] == id) {
                contains = true;
                break;
            }
        }
        if (contains) {
            return new Packet(id, data) {};
        } else {
            return new UtilPackets.EmptyPacket();
        }
    }

    @Override
    public boolean isIDRegistered(byte id) {
        return false;
    }


}
