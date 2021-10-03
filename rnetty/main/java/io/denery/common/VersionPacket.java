package io.denery.common;

/*
    Simple version packet, a couple of char bytes in UTF-8.
 */
public class VersionPacket extends BNPacket {
    public VersionPacket(byte[] data) {
        super(BNPackets.VERSION, data);
    }
}
