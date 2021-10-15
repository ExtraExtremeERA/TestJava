package io.denery.packets;

import io.denery.common.Packet;
import io.denery.common.UtilPackets;

/*
    Simple version packet, a couple of char bytes in UTF-8.
 */
public class VersionPacket extends Packet {
    public VersionPacket(byte[] data) {
        super((byte) 0x2, data);
    }
    public VersionPacket() {
        super((byte) 0x2);
    }
}
