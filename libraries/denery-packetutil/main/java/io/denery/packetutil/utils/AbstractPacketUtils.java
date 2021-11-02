package io.denery.packetutil.utils;

import io.denery.packetutil.Packet;
import reactor.core.publisher.Flux;

public abstract class AbstractPacketUtils {
    public abstract Flux<Packet> parsePacketStream(byte[] data) throws NullPointerException;

    public abstract Packet formatPacket(byte id, byte[] data);

    public abstract Packet identifyPacket(byte[] data);

    public abstract boolean isIDRegistered(byte id);
}
