package io.denery.transformers;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.*;

import static org.objectweb.asm.Opcodes.*;

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;

public record SampleClassTransformer(String targetClassName, ClassLoader targetClassLoader) implements ClassFileTransformer {
    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain, byte[] classfileBuffer) {

        final String dottedClassName = className.replace("/", ".");
        if (!dottedClassName.equals(targetClassName)) return classfileBuffer;

        System.out.println("Class in transform: " + dottedClassName + "\n");
        System.out.println("Target class: " + targetClassName + "\n");

        ClassWriter cw = null;
        if (loader.equals(targetClassLoader)) {
            try {
                ClassNode cn = new ClassNode(ASM9);
                ClassReader cr = new ClassReader(dottedClassName);
                cr.accept(cn,0);
                cn.methods.forEach(mn -> {
                    System.out.println("methods: " + mn.name);
                    if (mn.name.equals("compareTo")) {
                        InsnList insnl = new InsnList();
                        insnl.add(new FieldInsnNode(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;"));
                        insnl.add(new LdcInsnNode("Transformation Occurred!"));
                        insnl.add(new MethodInsnNode(INVOKEVIRTUAL, "java/io/PrintStream", "println",
                                "(Ljava/lang/String;)V", false));
                        mn.instructions.insert(insnl);
                        mn.maxStack += 1;
                    }
                });
                cw = new ClassWriter(cr, 0);
                cn.accept(cw);
            } catch (IOException e) {
                throw new RuntimeException("Cannot Transform Class: " + dottedClassName, e);
            }
        }
        assert cw != null;
        return cw.toByteArray();
    }
}
