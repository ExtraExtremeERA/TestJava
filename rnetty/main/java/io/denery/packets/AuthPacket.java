package io.denery.packets;

import io.denery.common.Packet;
import io.denery.common.UtilPackets;

/*
    An Authentication packet, represents Client name
    formatted by util/TokenFormat in char bytes in UTF-8.
 */
public class AuthPacket extends Packet {
    public AuthPacket(byte[] data) {
        super((byte) 0x1, data);
    }

    public AuthPacket() {
        super((byte) 0x1);
    }
}
