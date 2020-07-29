package com.kronos.doubletap.helper;

import com.kronos.doubletap.DoubleTabConfig;
import com.kronos.thread.plugin.visitor.ThreadPoolMethodVisitor;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.*;

public class InitBlockVisitor extends ThreadPoolMethodVisitor {
    private String owner;

    InitBlockVisitor(MethodVisitor mv, String owner) {
        super(mv);
        this.owner = owner;
    }

    @Override
    public void visitInsn(int opcode) {
        if ((opcode >= Opcodes.IRETURN && opcode <= Opcodes.RETURN)
                || opcode == Opcodes.ATHROW) {
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitTypeInsn(Opcodes.NEW, DoubleTabConfig.ByteCodeInjectClassName);
            mv.visitInsn(Opcodes.DUP);
            mv.visitMethodInsn(Opcodes.INVOKESPECIAL, DoubleTabConfig.ByteCodeInjectClassName,
                    "<init>", "()V", false);
            mv.visitFieldInsn(Opcodes.PUTFIELD, owner, "doubleTap",
                    String.format("L%s;", DoubleTabConfig.ByteCodeInjectClassName));
        }
        super.visitInsn(opcode);
    }

    public void visitMaxs(int maxStack, int maxLocals) {
        // The values 3 and 0 come from the fact that our instance
        // creation uses 3 stack slots to construct the instances
        // above and 0 local variables.
        final int ourMaxStack = 3;
        final int ourMaxLocals = 0;

        // now, instead of just passing original or our own
        // visitMaxs numbers to super, we instead calculate
        // the maximum values for both.
        super.visitMaxs(Math.max(ourMaxStack, maxStack), Math.max(ourMaxLocals, maxLocals));
    }
}
