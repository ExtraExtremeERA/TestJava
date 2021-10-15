package io.denery.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *  Bootstrapper where you can register your types of packets.
 */
public final class PacketBootstrap {
    private PacketBootstrap() {}
    private static PacketBootstrap instance = null;
    /**
     * @return PacketBootstrap instance, singleton pattern.
     */
    public static PacketBootstrap getInstance() {
        if (instance == null) {
            instance = new PacketBootstrap();
        }
        return instance;
    }

    /**
     *  An array where contained all registered types of packets.
     */
    public byte[] packetIDs = new byte[0];

    /**
     * @return Registered packet IDs as List.
     */
    public List<Byte> getPacketIDs() {
        List<Byte> list = new ArrayList<>();
        for (byte b : packetIDs) {
            list.add(b);
        }
        return list;
    }

    /**
     * @return Registered packet IDs as byte array.
     */
    public byte[] getPacketIDsArr() {
        return packetIDs;
    }

    /**
     * @return A new instance of PacketBuilder.
     */
    public PacketBuilder newPacketBuilder() {
        return new PacketBootstrap.PacketBuilder();
    }

    /**
     *  Class where registering of packets happens.
     */
    public class PacketBuilder {
        private PacketBuilder() {}

        /**
         * Registries user's packet type.
         *
         * @param packet user's packet.
         * @return returns PacketBuilder instance with packet ID registered.
         * @see #build() to apply all your packet types.
         */
        public PacketBuilder registerPacket(Packet packet) {
            if (packet.id() == 0x0) throw new RuntimeException("Cannot registry packet under 0x0 ID, because 0x0 ID is used to an Empty packet!");
            byte[] ids = new byte[instance.packetIDs.length + 1];
            ids[instance.packetIDs.length] = packet.id();
            System.arraycopy(instance.packetIDs, 0, ids, 0, instance.packetIDs.length);
            instance.packetIDs = ids;
            instance.packetIDs = removeIdentical(instance.packetIDs);
            return this;
        }

        /**
         * Removes identical packet IDs to avoid duplicates and save performance.
         *
         * @param data byte array you need to remove identical values.
         * @return filtered byte array.
         */
        private byte[] removeIdentical(byte[] data) {
            int[] ii = new int[data.length];
            for (int i = 0; i < data.length; i++) {
                ii[i] = data[i];
            }
            int[] iii = Arrays.stream(ii).distinct().toArray();
            byte[] bb = new byte[iii.length];
            for (int i = 0; i < iii.length; i++) {
                bb[i] = (byte) iii[i];
            }
            return bb;
        }

        /**
         *  Builds PacketBootstrap with your packet types.
         */
        public PacketBootstrap build() {
            return PacketBootstrap.this;
        }
    }
}
