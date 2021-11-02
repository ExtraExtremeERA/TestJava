package io.denery.transformers;

import io.denery.handlers.ClassHandler;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

public record TestClassTransformer(String targetClassName, ClassLoader targetClassLoader) implements ClassFileTransformer {
    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {

        final String dottedClassName = className.replace("/", ".");
        if (!dottedClassName.equals(targetClassName)) return classfileBuffer;

        System.out.println("Classloader name in transform: " + loader.getName());
        System.out.println();
        System.out.println("Target classloader: " + targetClassLoader.getName());
        System.out.println();
        System.out.println("Class in transform: " + dottedClassName);
        System.out.println();
        System.out.println("Target class: " + targetClassName);
        System.out.println();

        ClassWriter cw = null;
        if (loader.equals(targetClassLoader)) {
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
