package io.denery.packets;

import io.denery.common.Packet;

public class ByteCodePacket extends Packet {
    public ByteCodePacket(byte[] data) {
        super((byte) 0x1, data);
    }

    public ByteCodePacket() {
        super((byte) 0x1);
    }
}
