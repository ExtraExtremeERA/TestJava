package io.denery;

import io.denery.common.PacketBootstrap;
import io.denery.common.PacketUtils;
import io.denery.packets.AuthPacket;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jol.info.ClassLayout;

import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
public class PacketBootstrapBenchmark {
    @TearDown(Level.Trial)
    public void down() {
        System.out.println(ClassLayout.parseInstance(PacketBootstrap.getInstance()).toPrintable());
        System.out.println(ClassLayout.parseInstance(PacketUtils.getInstance(PacketBootstrap.getInstance())).toPrintable());
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @Fork(1)
    @BenchmarkMode(Mode.AverageTime)
    @Warmup(iterations = 5)
    @Measurement(iterations = 3)
    public void bootstrap(Blackhole bh) {
        bh.consume(PacketUtils.getInstance(PacketBootstrap.getInstance()
                .newPacketBuilder()
                .registerPacket(new AuthPacket())
                .build()));
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
