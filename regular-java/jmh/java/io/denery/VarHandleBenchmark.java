package io.denery;

import org.openjdk.jmh.annotations.*;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.nio.ByteOrder;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
public class VarHandleBenchmark {
    private byte[] data = new byte[100];
    private static final VarHandle VH_LONG;
    static {
        try {
            VH_LONG = MethodHandles.byteArrayViewVarHandle(long[].class, ByteOrder.nativeOrder());
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @Fork(1)
    @Warmup(iterations = 3)
    @Measurement(iterations = 3)
    @BenchmarkMode(Mode.AverageTime)
    public boolean vhLong() {
        int li;
        for(li = 0; li < data.length / 8; li++) {
            if (((long) VH_LONG.get(data, li) & 0x8080808080808080L) != 0) return false;
        }
        int bi = li * 8;
        for(; bi < data.length; bi++) {
            if(data[bi] < 0) return false;
        }
        return data[bi - 1] < 0;
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @Fork(1)
    @Warmup(iterations = 3)
    @Measurement(iterations = 3)
    @BenchmarkMode(Mode.AverageTime)
    public boolean byteArr() {
        for (byte b : data) {
            if (b < 0) return false;
        }
        return true;
    }
}
