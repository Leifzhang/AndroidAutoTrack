package com.kronos.thread.plugin.visitor;


import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.List;

public class ThreadPoolMethodVisitor extends MethodVisitor {

    public ThreadPoolMethodVisitor(MethodVisitor mv) {
        super(Opcodes.ASM5, mv);
    }


    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
        boolean isThreadPool = isThreadPool(opcode, owner, name, desc);
        if (isThreadPool) {
            JLog.info("owner:" + owner + " name:" + name + " desc:" + desc);
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "com/wallstreetcn/sample/utils/TestIOThreadExecutor",
                    "getTHREAD_POOL_SHARE",
                    "()Lcom/wallstreetcn/sample/utils/TestIOThreadExecutor;", itf);
        } else {
            super.visitMethodInsn(opcode, owner, name, desc, itf);
        }
    }

    @Override
    public void visitInsn(int opcode) {
        super.visitInsn(opcode);
    }

    @Override
    public void visitLineNumber(int line, Label start) {
        super.visitLineNumber(line, start);
    }

    boolean isThreadPool(int opcode, String owner, String name, String desc) {
        List<PoolEntity> list = ThreadPoolCreator.INSTANCE.getPoolList();
        for (PoolEntity poolEntity : list) {
            if (opcode != poolEntity.getCode()) {
                continue;
            }
            if (!owner.equals(poolEntity.getOwner())) {
                continue;
            }
            if (!name.equals(poolEntity.getName())) {
                continue;
            }
            if (!desc.equals(poolEntity.getDesc())) {
                continue;
            }
            return true;
        }
        return false;
    }

}
