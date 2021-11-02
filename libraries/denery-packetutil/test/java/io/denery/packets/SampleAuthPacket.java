package io.denery.packets;

import io.denery.packetutil.Packet;

public class SampleAuthPacket extends Packet {
    public SampleAuthPacket(byte[] data) {
        super((byte) 0x1, data);
    }

    public SampleAuthPacket() {
        super((byte) 0x1);
    }
}
