package io.denery.common;

/*
    An Authentication packet, represents Client name
    formatted by util/TokenFormat in char bytes in UTF-8.
 */
public class AuthPacket extends BNPacket {
    public AuthPacket(byte[] data) {
        super(BNPackets.AUTH, data);
    }
}
