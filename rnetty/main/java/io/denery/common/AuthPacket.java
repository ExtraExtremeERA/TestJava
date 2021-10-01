package io.denery.common;

public class AuthPacket extends BNPacket {
    public AuthPacket(byte[] data) {
        super(BNPackets.AUTH, data);
    }
}
