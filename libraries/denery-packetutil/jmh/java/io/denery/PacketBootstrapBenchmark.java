package io.denery;

import io.denery.packets.SampleAuthPacket;
import io.denery.packetutil.PacketBootstrap;
import io.denery.packetutil.utils.PacketUnsafeUtils;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jol.info.ClassLayout;

import java.util.concurrent.TimeUnit;

@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 5)
@Measurement(iterations = 3)
@State(Scope.Benchmark)
public class PacketBootstrapBenchmark {
    @TearDown(Level.Trial)
    public void down() {
        System.out.println(ClassLayout.parseInstance(PacketBootstrap.getInstance()).toPrintable());
        System.out.println(ClassLayout.parseInstance(PacketUnsafeUtils.getInstance(PacketBootstrap.getInstance())).toPrintable());
    }

    @Benchmark
    public void bootstrap(Blackhole bh) {
        bh.consume(PacketUnsafeUtils.getInstance(PacketBootstrap.getInstance()
                .newPacketBuilder()
                .registerPacket(new SampleAuthPacket())
                .build()));
    }
}
