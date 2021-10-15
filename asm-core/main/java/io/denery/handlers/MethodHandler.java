package io.denery.handlers;

import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.*;

public class MethodHandler extends MethodVisitor {
    public MethodHandler(MethodVisitor mv) {
        super(ASM9, mv);
    }

    @Override
    public void visitCode() {
        System.out.println("Calling method transformation...");
        mv.visitCode();
        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        mv.visitLdcInsn("WTFFFFFFF");
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
    }

    @Override
    public void visitMaxs(int maxStack, int maxLocals) {
        mv.visitMaxs(maxStack + 1, maxLocals);
    }
}
