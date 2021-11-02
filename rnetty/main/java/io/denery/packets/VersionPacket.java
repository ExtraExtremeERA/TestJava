package io.denery.packets;

import io.denery.packetutil.Packet;

public class VersionPacket extends Packet {
    public VersionPacket(byte[] data) {
        super((byte) 0x1, data);
    }

    public VersionPacket() {
        super((byte) 0x2);
    }
}
