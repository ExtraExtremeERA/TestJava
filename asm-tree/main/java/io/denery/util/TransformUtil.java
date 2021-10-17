package io.denery.util;

import io.denery.transformers.SampleClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.PrintWriter;
import java.lang.instrument.Instrumentation;

public class TransformUtil {
    public static void transformClass(String name, Instrumentation inst) {
        Class<?> targetCls = null;
        ClassLoader targetClassLoader = null;

        try {
            targetCls = Class.forName(name);
            targetClassLoader = targetCls.getClassLoader();
            System.out.println("Class: " + targetCls.getName());
            transform(targetCls, targetClassLoader, inst);
            return;
        } catch (Exception e) {
            System.out.println("Couldn't find class by name!");
        }

        for (Class<?> clazz : inst.getAllLoadedClasses()) {
            if (clazz.getName().equals(name)) {
                System.out.println("Class found: " + clazz.getName() + "\n");
                targetCls = clazz;
                targetClassLoader = targetCls.getClassLoader();
                transform(targetCls, targetClassLoader, inst);
                return;
            }
        }

        throw new RuntimeException("Failed to find class: " + name);
    }

    public static void transform(Class<?> clazz, ClassLoader classLoader, Instrumentation instrumentation) {
        try {
            System.out.println("transforming class: " + clazz.getName() + "\n");
            SampleClassTransformer tct = new SampleClassTransformer(clazz.getName(), classLoader);
            instrumentation.addTransformer(tct, true);
            try {
                instrumentation.retransformClasses(clazz);
            } catch (Exception e) {
                throw new RuntimeException("Transform failed for: " + clazz.getName(), e);
            }
        } catch (Exception e) {
            throw new RuntimeException("Cannot transform class!", e);
        }
    }

    public static byte[] traceClass(String name) {
        try {
            ClassReader cr = new ClassReader(name);
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
