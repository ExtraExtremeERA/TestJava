package io.denery;

import io.denery.common.Packet;
import io.denery.common.PacketBootstrap;
import io.denery.common.PacketUtils;
import io.denery.packets.AuthPacket;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import reactor.core.publisher.Flux;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
public class PacketFormatBenchmark {

    @Param({"1000"})
    public int iterations;

    public byte[] packetSolidStream = {49, 48, 48, 35, 49, 48, 49, 35, 49, 49, 48, 35, 49, 48, 49, 35, 49, 49, 52, 35, 49, 50, 49, 35, 1,
            49, 48, 48, 35, 49, 48, 49, 35, 49, 49, 48, 35, 49, 48, 49, 35, 49, 49, 52, 35, 49, 50, 49, 35, 1,
            49, 48, 48, 35, 49, 48, 49, 35, 49, 49, 48, 35, 49, 48, 49, 35, 49, 49, 52, 35, 49, 50, 49, 35, 1,
            49, 48, 48, 35, 49, 48, 49, 35, 49, 49, 48, 35, 49, 48, 49, 35, 49, 49, 52, 35, 49, 50, 49, 35, 1};
    public byte[] data = "denery".getBytes(StandardCharsets.UTF_8);
    public PacketUtils utils = PacketUtils.getInstance(PacketBootstrap.getInstance()
            .newPacketBuilder()
            .registerPacket(new AuthPacket())
            .build());
    public Packet packet = utils.formatPacket((byte) 0x1, data);

    @Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @Fork(1)
    @BenchmarkMode(Mode.AverageTime)
    @OperationsPerInvocation(1000)
    @Warmup(iterations = 3)
    @Measurement(iterations = 3)
    public void parseStream(Blackhole bh) {
        for (int i = 0; i < iterations; i++) {
            bh.consume(utils.parsePacketStream(packetSolidStream));
        }
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @Fork(1)
    @BenchmarkMode(Mode.AverageTime)
    @OperationsPerInvocation(1000)
    @Warmup(iterations = 3)
    @Measurement(iterations = 3)
    public void format(Blackhole bh) {
        for (int i = 0; i < iterations; i++) {
            bh.consume(utils.formatPacket((byte) 0x1, data));
        }
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @Fork(1)
    @BenchmarkMode(Mode.AverageTime)
    @Warmup(iterations = 3)
    @Measurement(iterations = 3)
    public void parsePacket(Blackhole bh) {
        bh.consume(packet.parsePacket());
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.SECONDS)
    @Fork(1)
    @BenchmarkMode(Mode.Throughput)
    @Warmup(iterations = 3)
    @Measurement(iterations = 3)
    public void fullFormattingCycleThroughput(Blackhole bh) {
        bh.consume(utils.parsePacketStream(utils.formatPacket((byte) 0x1, data).getData()).subscribe(packet1 -> bh.consume(packet1.parsePacket())));
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @Fork(1)
    @BenchmarkMode(Mode.AverageTime)
    @Warmup(iterations = 3)
    @Measurement(iterations = 3)
    public void fullFormattingCycleTime(Blackhole bh) {
        bh.consume(utils.parsePacketStream(utils.formatPacket((byte) 0x1, data).getData()).subscribe(packet1 -> bh.consume(packet1.parsePacket())));
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(PacketFormatBenchmark.class.getSimpleName())
                .forks(1)
                .threads(4)
                .build();

        new Runner(opt).run();
    }
}
