package io.denery.util;

import com.sun.tools.attach.VirtualMachine;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.util.TraceClassVisitor;
import org.threadly.concurrent.UnfairExecutor;
import org.threadly.concurrent.future.ListenableFuture;
import org.threadly.concurrent.wrapper.limiter.ExecutorLimiter;

import java.io.File;
import java.io.PrintWriter;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.net.URISyntaxException;
import java.util.Optional;

public class ModificationUtils {
    public static void modify(Class<?> transformer, Class<?>[] classes) throws URISyntaxException {
        UnfairExecutor ue = new UnfairExecutor(classes.length);
        ExecutorLimiter el = new ExecutorLimiter(ue, Runtime.getRuntime().availableProcessors() * 2);
        el.execute();
        for (Class<?> clazz : classes) {
            ue.submit(() -> {
                Optional<String> jvmProcessOpt = getJVMProcessPIDByClass(clazz);

                if (jvmProcessOpt.isEmpty()) {
                    throw new RuntimeException("There is no JVM with this descriptor!");
                }

                String agentFile;
                try {
                    agentFile = new File(ModificationUtils.class.getProtectionDomain().getCodeSource().getLocation()
                            .toURI()).getAbsolutePath();
                } catch (URISyntaxException e) {
                    throw new RuntimeException("Cannot get jar with agent!", e);
                }

                try {
                    String jvmPid = jvmProcessOpt.get();
                    VirtualMachine jvm = VirtualMachine.attach(jvmPid);
                    jvm.loadAgent(agentFile, transformer.getName() + "-" + clazz.getName());
                    jvm.detach();
                } catch (Exception e) {
                    throw new RuntimeException("Cannot attach JVM somehow!" + e + "\n");
                }
            });
        }

        ue.shutdown();
    }

    public static Optional<String> getJVMProcessPIDByClass(Class<?> clazz) {
        return Optional.ofNullable(VirtualMachine.list()
                .stream()
                .filter(jvm -> {
                    return jvm.displayName().contains(clazz.getSimpleName());
                })
                .findFirst().get().id());
    }

    public static void transformClass(Class<?> targetCls, ClassFileTransformer transformer, Instrumentation inst) {
        final String targetClsName = targetCls.getName();
        try {
            transform(targetCls, transformer, inst);
            return;
        } catch (Exception e) {
            System.out.println("Couldn't find class by name!");
        }

        for (Class<?> clazz : inst.getAllLoadedClasses()) {
            if (clazz.getName().equals(targetClsName)) {
                targetCls = clazz;
                transform(targetCls, transformer, inst);
                return;
            }
        }

        throw new RuntimeException("Failed to find class: " + targetClsName);
    }

    public static void transform(Class<?> clazz, ClassFileTransformer transformer, Instrumentation instrumentation) {
        try {
            instrumentation.addTransformer(transformer, true);
            try {
                instrumentation.retransformClasses(clazz);
            } catch (Exception e) {
                throw new RuntimeException("Transform failed for: " + clazz.getName(), e);
            }
        } catch (Exception e) {
            throw new RuntimeException("Cannot transform class!", e);
        }
    }

    public static byte[] traceClass(Class<?> clazz) {
        try {
            ClassReader cr = new ClassReader(clazz.getName());
            ClassWriter cw = new ClassWriter(cr, 0);
            PrintWriter pw = new PrintWriter(System.out);
            TraceClassVisitor tcv = new TraceClassVisitor(cw, pw);
            cr.accept(tcv, 0);
            return cw.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("cannot trace class!", e);
        }
    }
}
