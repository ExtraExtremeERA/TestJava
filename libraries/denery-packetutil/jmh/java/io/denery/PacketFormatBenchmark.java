package io.denery;

import io.denery.packets.SampleAuthPacket;
import io.denery.packetutil.Packet;
import io.denery.packetutil.PacketBootstrap;
import io.denery.packetutil.utils.PacketUnsafeUtils;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@BenchmarkMode(Mode.AverageTime)
@OperationsPerInvocation(1000)
@Warmup(iterations = 3)
@Measurement(iterations = 3)
@State(Scope.Benchmark)
/**
 * Some benchmarks are incorrect, fix later.
 */
public class PacketFormatBenchmark {

    @Param({"1000"})
    public int iterations;

    public byte[] packetSolidStream = {49, 48, 48, 35, 49, 48, 49, 35, 49, 49, 48, 35, 49, 48, 49, 35, 49, 49, 52, 35, 49, 50, 49, 35, 1,
            49, 48, 48, 35, 49, 48, 49, 35, 49, 49, 48, 35, 49, 48, 49, 35, 49, 49, 52, 35, 49, 50, 49, 35, 1,
            49, 48, 48, 35, 49, 48, 49, 35, 49, 49, 48, 35, 49, 48, 49, 35, 49, 49, 52, 35, 49, 50, 49, 35, 1,
            49, 48, 48, 35, 49, 48, 49, 35, 49, 49, 48, 35, 49, 48, 49, 35, 49, 49, 52, 35, 49, 50, 49, 35, 1};
    public byte[] data = "denery".getBytes(StandardCharsets.UTF_8);
    public PacketUnsafeUtils utils = PacketUnsafeUtils.getInstance(PacketBootstrap.getInstance()
            .newPacketBuilder()
            .registerPacket(new SampleAuthPacket())
            .build());
    public Packet packet = utils.formatPacket((byte) 0x1, data);

    @Benchmark
    public void parseStream(Blackhole bh) {
        for (int i = 0; i < iterations; i++) {
            bh.consume(utils.parsePacketStream(packetSolidStream));
        }
    }

    @Benchmark
    public void format(Blackhole bh) {
        for (int i = 0; i < iterations; i++) {
            bh.consume(utils.formatPacket((byte) 0x1, data));
        }
    }

    @Benchmark
    public void parsePacket(Blackhole bh) {
        bh.consume(packet.parsePacket());
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.SECONDS)
    @BenchmarkMode(Mode.Throughput)
    public void fullFormattingCycleThroughput(Blackhole bh) {
        bh.consume(utils.parsePacketStream(utils.formatPacket((byte) 0x1, data).getData()).subscribe(packet1 -> bh.consume(packet1.parsePacket())));
    }

    @Benchmark
    public void fullFormattingCycleTime(Blackhole bh) {
        bh.consume(utils.parsePacketStream(utils.formatPacket((byte) 0x1, data).getData()).subscribe(packet1 -> bh.consume(packet1.parsePacket())));
    }
}
