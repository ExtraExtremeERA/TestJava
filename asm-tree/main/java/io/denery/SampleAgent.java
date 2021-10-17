package io.denery;

import com.sun.tools.attach.VirtualMachine;
import io.denery.util.TransformUtil;

import java.io.File;
import java.lang.instrument.Instrumentation;
import java.net.URISyntaxException;
import java.util.Optional;

public class SampleAgent {
    public static void premain(String agentArgs, Instrumentation inst) {
        System.out.println("[Agent] In premain method");
    }

    public static void agentmain(String agentArgs, Instrumentation inst) {
        System.out.println("[Agent] In agentmain method");
        SampleClass sc = new SampleClass();
        System.out.println("First execution: " + sc.compareTo(new SampleClass()) + " <<<<<<<<<<<<<<<<<<<<<<<<<\n\n");

        System.out.println("Is redefine supported: " + inst.isRedefineClassesSupported());
        System.out.println("Is retransform supported: " + inst.isRetransformClassesSupported() + "\n");

        try {
            TransformUtil.transformClass("io.denery.SampleClass", inst);
        } catch (Exception e) {
            throw new RuntimeException("Cannot do the transformation!", e);
        }
    }

    public void run(String[] args) throws URISyntaxException {
        //Getting a JVM PID in which program is executing.
        Optional<String> jvmProcessOpt = Optional.ofNullable(VirtualMachine.list()
                .stream()
                .filter(jvm -> {
                    System.out.println("jvm: " + jvm.displayName());
                    return jvm.displayName().contains("SampleLauncher");
                })
                .findFirst().get().id());
        if (jvmProcessOpt.isEmpty()) {
            throw new RuntimeException("There is no JVM with this descriptor!");
        }

        //Getting a Jar where the program is executing.
        String agentFile = new File(SampleLauncher.class.getProtectionDomain().getCodeSource().getLocation()
                .toURI()).getAbsolutePath();

        try {
            String jvmPid = jvmProcessOpt.get();
            System.out.println("Attaching to target JVM with PID: " + jvmPid);
            VirtualMachine jvm = VirtualMachine.attach(jvmPid);
            jvm.loadAgent(agentFile);
            jvm.detach();
            System.out.println("Attached to target JVM and loaded Java agent successfully");
        } catch (Exception e) {
            throw new RuntimeException("Cannot attach JVM somehow!" + e + "\n");
        }

    }
}
