package io.denery;

import org.openjdk.jcstress.annotations.*;
import org.openjdk.jcstress.infra.results.II_Result;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;

@JCStressTest
@Outcome(expect = Expect.ACCEPTABLE_INTERESTING)
@Outcome(id = "0, 0", expect = Expect.FORBIDDEN,  desc = "Violates sequential consistency")
@State
public class RaceByteArrVarHandleTest {
    private static final VarHandle VH_BB;
    public byte[] bb = new byte[2];
    static {
        try {
            VH_BB = MethodHandles.arrayElementVarHandle(byte[].class);
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    @Actor
    public void actor1(II_Result r) {
        VH_BB.setVolatile(bb, 0, (byte) 1);
        r.r1 = (int) VH_BB.getVolatile(bb, 1);
    }

    @Actor
    public void actor2(II_Result r) {
        VH_BB.setVolatile(bb, 1, (byte) 1);
        r.r2 = (int) VH_BB.getVolatile(bb, 0);
    }

}
