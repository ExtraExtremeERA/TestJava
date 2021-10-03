package io.denery.common;

import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
/*
    A Super class for all packet types, has util functions,
    for example: identifying, parsing each packet, parsing from raw byte stream, etc.
 */
public abstract class BNPacket {
    private byte id;
    private byte[] data;
    public BNPacket(BNPackets packet, byte[] data) {
        this.id = packet.getId();
        this.data = data;
    }

     /*
        Every packet has type identifier, it is a getter of identifier.
     */
    public byte id() {
        return id;
    }

     /*
        Every packet has data in it, it is a getter of data.
     */
    public byte[] getData() {
        return data;
    }

    /*
     Simple packet parser of a simple packet format!
    */
    public byte[] parsePacket() {
        int i = 0;
        byte[] formattedData = new byte[data.length - 1];
        for (byte b : data) {
            if (i < data.length - 1) {
                formattedData[i] = data[i];
                i++;
            }
        }
        return formattedData;
    }

    /*
     When client/server send Flux with byte arrays, all byte arrays comes in one big byte array.
     This method divide this big byte array in packets.
    */
    public static Flux<BNPacket> parsePacketStream(byte[] data) throws NullPointerException {
        List<BNPacket> packetList = new ArrayList<>();
        Flux<BNPacket> packets = null;
        int i = 0;
        int x = 0;
        byte[] rawData = new byte[data.length];
        for (byte b : data) {
            if (!identifyPacketByte(b)) {
                rawData[i] = data[i];
                i++;
            } else {
                rawData[i] = data[i];
                i++;
                    int y = 0;
                    for (byte b1 : rawData) {
                        y++;
                    }
                    int a = 0;
                    for (byte b1 : rawData) {
                        if (b1 != 0) break;
                        a++;
                    }
                    int c = 0;
                    for (byte b1 : rawData) {
                        if (b1 == 0) c++;
                    }
                    byte[] formattedData = new byte[y - c];
                    int z = 0;
                    for (byte b1 : rawData) {
                        if (!(b1 == 0)) {
                            formattedData[z] = rawData[z + a];
                            z++;
                        }
                    }
                    Flux.just(identifyPacket(formattedData)).log().subscribe(packetList::add);
                    rawData = new byte[data.length - x];
            }
        }
        packets = Flux.push(sink -> {
            for (BNPacket packet : packetList) {
                sink.next(packet);
            }
        });
        return packets;
    }

     /*
        Creates a new packet from byte array and returns it as byte array.
     */
    public static byte[] formatPacketBytes(byte[] data, BNPackets packet) {
        int i = 0;
        byte[] formattedData = new byte[data.length + 1];
        formattedData[0] = packet.getId();
        for (byte b : data) {
            formattedData[i + 1] = data[i];
            i++;
        }
        return formattedData;
    }

     /*
        Creates a new packet from byte array and returns it as BNPacket instance.
     */
    public static BNPacket formatPacket(byte[] data, BNPackets packet) {
        int i = 0;
        byte[] formattedData = new byte[data.length + 1];
        formattedData[data.length] = packet.getId();
        for (byte b : data) {
            formattedData[i] = data[i];
            i++;
        }
        return identifyPacket(formattedData);
    }

     /*
        Identifies packet from byte array and returns it as a BNPacket Instance.
     */
    public static BNPacket identifyPacket(byte[] bytes) throws NullPointerException {
        return switch (bytes[bytes.length - 1]) {
            case (byte) 0x1 -> new AuthPacket(bytes);
            case (byte) 0x2 -> new VersionPacket(bytes);
            default -> null;
        };
    }

     /*
        Returns true if a packet identifier of this byte exists.
     */
    public static boolean identifyPacketByte(byte b) {
        return switch (b) {
            case 0x1, 0x2 -> true;
            default -> false;
        };
    }
}
