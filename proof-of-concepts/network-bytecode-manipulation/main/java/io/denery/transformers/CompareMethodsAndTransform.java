package io.denery.transformers;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;

public final class CompareMethodsAndTransform implements ClassFileTransformer {
    private final Class<?> clazz;
    private final Class<?> clazz1;
    /**
     * @param clazz class where you need to compare methods and transform.
     * @param clazz1 class which you're comparing to.
     */
    public CompareMethodsAndTransform(Class<?> clazz, Class<?> clazz1) {
        this.clazz = clazz;
        this.clazz1 = clazz1;
    }
    @Override
    public byte[] transform(ClassLoader loader, String className,
                            Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
                            byte[] classfileBuffer) {

        final String dottedClassName = className.replace("/", ".");
        if (!dottedClassName.equals(clazz.getName())) return classfileBuffer;

        ClassWriter cw = null;
        if (clazz.getClassLoader().equals(loader)) {
            try {
                ClassNode cn = new ClassNode();
                ClassNode cn1 = new ClassNode();
                ClassReader cr = new ClassReader(clazz.getName());
                ClassReader cr1 = new ClassReader(clazz1.getName());

                cr.accept(cn, 0);
                cr1.accept(cn1, 0);
                cn.methods.forEach(mn -> {
                    cn1.methods.forEach(mn1 -> {
                        if (mn1.name.equals(mn.name) || mn1.desc.equals(mn.desc) || mn1.access == mn.access) {
                            mn.instructions = mn1.instructions;
                        }
                    });
                });
                cw = new ClassWriter(cr, 0);
                cn.accept(cw);
            } catch (IOException e) {
                throw new RuntimeException("Cannot Transform Class: " + dottedClassName, e);
            }
        }
        if (cw == null) return classfileBuffer;
        return cw.toByteArray();
    }
}
