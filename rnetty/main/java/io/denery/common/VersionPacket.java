package io.denery.common;

public class VersionPacket extends BNPacket {
    public VersionPacket(byte[] data) {
        super(BNPackets.VERSION, data);
    }
}
