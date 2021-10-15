package io.denery;

import org.openjdk.jcstress.annotations.*;
import org.openjdk.jcstress.infra.results.I_Result;

@JCStressTest
@Outcome(id = "1", expect = Expect.ACCEPTABLE_INTERESTING, desc = "One update lost: atomicity failure.")
@Outcome(id = "2", expect = Expect.ACCEPTABLE, desc = "Actors updated independently.")
@State
public class ASMRaceTest {
    //Soon Race tests.
    int v;

    @Actor
    public void actor1() {
        v++;
    }

    @Actor
    public void actor2() {
        v++;
    }

    @Arbiter
    public void arbiter(I_Result r) {
        r.r1 = v;
    }
}
