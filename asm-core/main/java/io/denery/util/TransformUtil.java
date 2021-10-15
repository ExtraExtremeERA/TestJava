package io.denery.util;

import io.denery.transformers.TestClassTransformer;
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
            System.out.println("Class: " + name);
            targetCls = Class.forName(name);
            targetClassLoader = targetCls.getClassLoader();
            transform(targetCls, targetClassLoader, inst);
            return;
        } catch (Exception e) {
            System.out.println("Couldn't find class by name!");
        }

        for(Class<?> clazz : inst.getAllLoadedClasses()) {
            if(clazz.getName().equals(name)) {
                System.out.println("Class found: " + clazz.getName());
                System.out.println();
                System.out.println("Class found loader: " + clazz.getClassLoader().getName());
                System.out.println();
                targetCls = clazz;
                targetClassLoader = targetCls.getClassLoader();
                transform(targetCls, targetClassLoader, inst);
                return;
            }
        }

        throw new RuntimeException("Failed to find class [" + name + "]");
    }

    public static void transform(Class<?> clazz, ClassLoader classLoader, Instrumentation instrumentation) {
        try {
            TestClassTransformer tct = new TestClassTransformer(clazz.getName(), classLoader);
            System.out.println("transforming class: " + clazz.getName());
            System.out.println();
            System.out.println("with class loader: " + clazz.getClassLoader().getName());
            System.out.println();
            instrumentation.addTransformer(tct, true);
            try {
                instrumentation.retransformClasses(clazz);
            } catch (Exception ex) {
                throw new RuntimeException(
                        "Transform failed for: [" + clazz.getName() + "]", ex);
            }
        } catch (Exception e) {
            throw new RuntimeException("cannot transform class!", e);
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
            throw new RuntimeException("cannot transform class!", e);
        }
    }
}
