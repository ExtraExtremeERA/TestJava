package io.denery.common;

import reactor.core.publisher.Flux;
import reactor.core.publisher.ParallelFlux;
import reactor.core.scheduler.Schedulers;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.BiFunction;
import java.util.stream.Stream;

/**
 *  Utils allows you to interact with packets, you can inherit this class to do custom
 *  packet identification/formatting and many other algorithms.
 */
public class PacketUtils {
    private final PacketBootstrap bootstrap;
    /**
     * @param bootstrap PacketBootstrap instance.
     */
    private PacketUtils(PacketBootstrap bootstrap) {
        this.bootstrap = bootstrap;
    }

    private static PacketUtils instance;
    /** Gives you PacketUtils instance, example of usage:
     * <pre>{@code
     * private static final PacketUtils utils = PacketUtils.getInstance(PacketBootstrap.getInstance()
     *             .newPacketBuilder()
     *             .registerPacket(new AuthPacket())
     *             .registerPacket(new VersionPacket())
     *             .build());
     * }</pre>
     * @param bootstrap {@code PacketBootstrap} instance.
     * @see PacketBootstrap#getInstance() getting instance and registering your packet types.
     */
    public static PacketUtils getInstance(PacketBootstrap bootstrap) {
        if (instance == null) {
            instance = new PacketUtils(bootstrap);
        }
        return instance;
    }

    /**
     * When client/server receives byte array, all data comes in one big byte array,
     * where all packets are one big solid stream of bytes.
     * This method divide this big byte array in packets and returns it as sequence of packets.
     * Example in .handle method in {@code TcpServer} or in {@code TcpClient} :
     *
     * <pre>{@code
     *      .handle((nettyInbound, nettyOutbound) -> nettyInbound.receive().asByteArray().handle((packets, sink) -> {
     *                  utils.parsePacketStream(packets).subscribe(packet -> {
     *                      if (packet.id() == 0x1) {
     *                         System.out.println(packet.parsePacket());
     *                      }
     *
     *                      if (packet.id() == 0x2) {
     *                         System.out.println(new String(packet.parsePacket(), StandardCharsets.UTF_8));
     *                      }
     *                  });
     *                 sink.next(packets);
     *       }).then())
     * }</pre>
     *
     * @param data Solid stream of bytes that client/server receives.
     * @return A sequence of Packets.
     *
     * @see Packet#parsePacket() get data while handling/subscribing these packets.
     */
    public Flux<Packet> parsePacketStream(byte[] data) throws NullPointerException {
        return Flux.create(s -> {
            int i = 0;
            byte[] rawData = new byte[data.length];

            for (byte b : data) {
                    rawData[i] = data[i];
                    i++;

                if (isIDRegistered(b)) {
                        int a = 0;
                        for (byte b1 : rawData) {
                            if (b1 != 0) break;
                            a++;
                        }
                        int c = 0;
                        for (byte b1 : rawData) {
                            if (b1 == 0) c++;
                        }
                        byte[] formattedData = new byte[rawData.length - c];
                        int z = 0;
                        for (byte b1 : rawData) {
                            if (!(b1 == 0)) {
                                formattedData[z] = rawData[z + a];
                                z++;
                            }
                        }
                    s.next(identifyPacket(formattedData));
                    rawData = new byte[data.length];
                }
            }
        });
    }

    /**
     * Creates a new packet from byte array and returns it as a Packet instance.
     *
     * @param id packet identifier.
     * @param data packet data.
     */
    public Packet formatPacket(byte id, byte[] data) {
        byte[] formattedData = new byte[data.length + 1];
        formattedData[data.length] = id;
        System.arraycopy(data, 0, formattedData, 0, data.length);
        return identifyPacket(formattedData);
    }

    /**
     * Identifies packet from byte array and returns it as a Packet instance.
     *
     * @param data byte array generated from method #formatPacketBytes(byte[] data, Packets packet); or
     *              formatPacket().getData();
     * @return Packet instance which you can cast to your packet type and parse data from it.
     * If data isn't any of your packet types then it sends an EmptyPacket utility type
     * to avoid NullPointerException, but you should handle these packets in your app.
     * @see #identifyPacket(byte b, byte[] data) specific cases when you have custom parsing/formatting.
     */
    public Packet identifyPacket(byte[] data) {
        return identifyPacket(data[data.length - 1], data);
    }

    /**
     *  Identifies packet from ID and byte array with its data (must include identifier).
     *
     * @param id packet identifier.
     * @param data packet data. (must include identifier)
     *
     * @return Packet instance which you can cast to your packet type and parse data from it.
     * If data isn't any of your packet types then it sends an EmptyPacket utility type
     * to avoid NullPointerException, but you should handle these packets in your app.
     * @see #identifyPacket(byte[]) most of the cases.
     */
    public Packet identifyPacket(byte id, byte[] data) {
        boolean contains = false;

        for(int i = 0; i < bootstrap.packetIDs.length; i++) {
            if (bootstrap.packetIDs[i] == id) {
                contains = true;
                break;
            }
        }
        if (contains) {
            return new Packet(id, data) {};
        } else {
            return new UtilPackets.EmptyPacket();
        }
    }

    /**
     * @param id identifier of packet.
     * @return true if ID is registered.
     */
    public boolean isIDRegistered(byte id) {
        boolean contains = false;
        for(int i = 0; i < bootstrap.packetIDs.length; i++) {
            if (bootstrap.packetIDs[i] == id) {
                contains = true;
                break;
            }
        }
        return contains;
    }
}