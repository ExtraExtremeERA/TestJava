package io.denery;

import io.denery.sampleClasses.Bebra;
import io.denery.util.TransformUtil;

import java.lang.instrument.Instrumentation;

public class PreMain {
    public static void premain(String agentArgs, Instrumentation inst) {
        System.out.println("[Agent] In premain method");
        System.out.println();

        Bebra bebra = new Bebra();
        System.out.println("First execution: " + bebra.getAbobification() + " <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
        System.out.println();
        System.out.println();

        System.out.println("Is redefine supported: " + inst.isRedefineClassesSupported());
        System.out.println("Is retransform supported: " + inst.isRetransformClassesSupported());
        System.out.println();
        try {
            TransformUtil.transformClass("io.denery.sampleClasses.Bebra", inst);
        } catch (Exception e) {
            throw new RuntimeException("Cannot init application!", e);
        }
    }
}
