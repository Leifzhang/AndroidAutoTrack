package com.kronos.doubletap.helper;


import com.kronos.doubletap.DoubleTabConfig;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class CheckVisitor extends MethodVisitor {
    private String owner;

    CheckVisitor(MethodVisitor mv, String owner) {
        super(Opcodes.ASM5, mv);
        this.owner = owner;
    }

    @Override
    public void visitCode() {
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitFieldInsn(Opcodes.GETFIELD, owner, "doubleTap",
                String.format("L%s;", DoubleTabConfig.ByteCodeInjectClassName));
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, DoubleTabConfig.ByteCodeInjectClassName,
                DoubleTabConfig.ByteCodeInjectFunctionName, "()Z", false);
        Label label = new Label();
        mv.visitJumpInsn(Opcodes.IFNE, label);
        mv.visitInsn(Opcodes.RETURN);
        mv.visitLabel(label);
        super.visitCode();
    }
}
