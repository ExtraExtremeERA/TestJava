package io.denery;

import org.objectweb.asm.*;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.Arrays;

import static org.objectweb.asm.Opcodes.*;
import static org.objectweb.asm.Type.*;

public class Main {
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
            transformClass("io.denery.Bebra", inst);
        } catch (Exception e) {
            throw new RuntimeException("Cannot init application!", e);
        }
    }

    public static void agentmain(String agentArgs, Instrumentation inst) {
        System.out.println("[Agent] In agentmain method");
    }

    private static void transformClass(String name, Instrumentation inst) {
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

    private static byte[] traceClass(String name) {
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

    //Call this app by "java -javaagent:asm.jar -jar asm.jar" to Jar in build/libs
    public static void main(String[] args) {
        System.out.println("[Agent] In main method");
        System.out.println();
        Bebra bebra = new Bebra();
        System.out.println("Second execution: " + bebra.getAbobification() + " <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
        //traceClass("io.denery.Bebra");
    }

    static class TestClassTransformer implements ClassFileTransformer {

        private final String targetClassName;
        private final ClassLoader targetClassLoader;
        public TestClassTransformer(String targetClassName, ClassLoader targetClassLoader) {
            this.targetClassName = targetClassName;
            this.targetClassLoader = targetClassLoader;
        }


        @Override
        public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                                ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {

            final String dottedClassName = className.replace("/", ".");

            System.out.println("Classloader name in transform: " + loader.getName());
            System.out.println();
            System.out.println("Target classloader: " + targetClassLoader.getName());
            System.out.println();
            System.out.println("Class in transform: " + dottedClassName);
            System.out.println();
            System.out.println("Target class: " + targetClassName);
            System.out.println();

            ClassWriter cw = null;
            if (dottedClassName.equals(targetClassName)
                    && loader.equals(targetClassLoader)) {
                try {
                    ClassReader cr = new ClassReader(dottedClassName);
                    cw = new ClassWriter(cr, 0);
                    ClassHandler ch = new ClassHandler(cw);
                    cr.accept(ch, 0);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            assert cw != null;
            return cw.toByteArray();
        }
    }

    static class ClassHandler extends ClassVisitor {
        public ClassHandler(ClassVisitor cv) {
            super(ASM9, cv);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
            if (mv != null && name.equals("getAbobification")) {
                mv = new MethodHandle(mv);
            }
            return mv;
        }
    }

    static class MethodHandle extends MethodVisitor {
        public MethodHandle(MethodVisitor mv) {
            super(ASM9, mv);
        }

        @Override
        public void visitCode() {
            System.out.println("Calling method transformation...");
            mv.visitCode();
            mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            mv.visitLdcInsn("abobification...");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
        }

        @Override
        public void visitMaxs(int maxStack, int maxLocals) {
            mv.visitMaxs(maxStack + 1, maxLocals);
        }
    }
}
