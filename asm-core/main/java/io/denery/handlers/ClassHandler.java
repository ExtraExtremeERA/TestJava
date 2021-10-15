package io.denery.handlers;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.ASM9;

public class ClassHandler extends ClassVisitor {
    public ClassHandler(ClassVisitor cv) {
        super(ASM9, cv);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
        if (mv != null && name.equals("getAbobification")) {
            mv = new MethodHandler(mv);
        }
        return mv;
    }
}
