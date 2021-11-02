package io.denery;

import it.unimi.dsi.fastutil.doubles.Double2ByteOpenHashMap;
import it.unimi.dsi.fastutil.doubles.DoubleBigArrayBigList;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jol.info.ClassLayout;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

//Will be complemented later with rise of other examples...
public class BigArrayBenchmark {

    @State(Scope.Benchmark)
    public static class FastUtilSpecific {
        @TearDown(Level.Trial)
        public void down() {
            System.out.println(ClassLayout.parseInstance(this).toPrintable());
        }

        @Benchmark
        @OutputTimeUnit(TimeUnit.NANOSECONDS)
        @Fork(1)
        @BenchmarkMode(Mode.AverageTime)
        @Warmup(iterations = 3)
        @Measurement(iterations = 3)
        public void bigArray() {
            DoubleBigArrayBigList doubleBigArr = new DoubleBigArrayBigList();
        }
    }

    @State(Scope.Benchmark)
    public static class FastUtil {
        @TearDown(Level.Trial)
        public void down() {
            System.out.println(ClassLayout.parseInstance(this).toPrintable());
        }

        @Benchmark
        @OutputTimeUnit(TimeUnit.NANOSECONDS)
        @Fork(1)
        @BenchmarkMode(Mode.AverageTime)
        @Warmup(iterations = 3)
        @Measurement(iterations = 3)
        @OperationsPerInvocation(1000)
        public void iterateFast(Blackhole bh) {
            Double2ByteOpenHashMap openHashMap = new Double2ByteOpenHashMap(1000);
            for (double i = 0.0; i < 100.0; i = i + 0.1) {
                openHashMap.addTo(i, (byte) 8);
                bh.consume(openHashMap.get(i));
            }
        }
    }

    @State(Scope.Benchmark)
    public static class UsualUtil {
        @TearDown(Level.Trial)
        public void down() {
            System.out.println(ClassLayout.parseInstance(this).toPrintable());
        }

        @Benchmark
        @OutputTimeUnit(TimeUnit.NANOSECONDS)
        @Fork(1)
        @BenchmarkMode(Mode.AverageTime)
        @Warmup(iterations = 3)
        @Measurement(iterations = 3)
        @OperationsPerInvocation(1000)
        public void iterateUsual(Blackhole bh) {
            Map<Double, Byte> hashMap = new HashMap<>(1000);
            for (double i = 0.0; i < 100.0; i = i + 0.1) {
                hashMap.put(i, (byte) 8);
                bh.consume(hashMap.get(i));
            }
        }
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(BigArrayBenchmark.class.getSimpleName())
                .forks(1)
                .threads(4)
                .build();

        new Runner(opt).run();
    }
}
