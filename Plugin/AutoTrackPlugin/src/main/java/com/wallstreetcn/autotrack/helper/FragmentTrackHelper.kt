package com.wallstreetcn.autotrack.helper

import org.objectweb.asm.Opcodes
import org.objectweb.asm.Opcodes.INVOKESTATIC
import org.objectweb.asm.Opcodes.INVOKEVIRTUAL
import org.objectweb.asm.tree.*

/**
 * @Author LiABao
 * @Since 2021/1/6
 */
fun ClassNode.insert(methodNode: MethodNode) {
    val instructions = methodNode.instructions
    instructions?.iterator()?.forEach {
        if ((it.opcode >= Opcodes.IRETURN && it.opcode <= Opcodes.RETURN) || it.opcode == Opcodes.ATHROW) {
            instructions.insertBefore(
                    it, FieldInsnNode(Opcodes.GETSTATIC, "com/wallstreetcn/testmodule/KronosContext",
                    "INSTANCE", "Lcom/wallstreetcn/testmodule/KronosContext;")
            )
            instructions.insertBefore(
                    it, MethodInsnNode(Opcodes.INVOKEVIRTUAL, "com/wallstreetcn/testmodule/KronosContext",
                    "getApp", "()Landroid/app/Application;", false)
            )
            instructions.insertBefore(
                    it, TypeInsnNode(Opcodes.NEW, "java/lang/StringBuilder")
            )
            instructions.insertBefore(
                    it, InsnNode(Opcodes.DUP)
            )
            instructions.insertBefore(
                    it, MethodInsnNode(
                    Opcodes.INVOKESPECIAL, "java/lang/StringBuilder",
                    "<init>", "()V", false)
            )
            instructions.insertBefore(it, VarInsnNode(Opcodes.ALOAD, 0))
            instructions.insertBefore(
                    it, MethodInsnNode(INVOKEVIRTUAL, "java/lang/Object",
                    "getClass", "()Ljava/lang/Class;", false)
            )
            instructions.insertBefore(
                    it, MethodInsnNode(
                    INVOKEVIRTUAL, "java/lang/Class", "getName",
                    "()Ljava/lang/String;", false
            )
            )
            instructions.insertBefore(
                    it, MethodInsnNode(
                    INVOKEVIRTUAL, "java/lang/StringBuilder",
                    "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false
            )
            )
            instructions.insertBefore(it, VarInsnNode(Opcodes.ILOAD, 1))
            instructions.insertBefore(
                    it, MethodInsnNode(
                    INVOKEVIRTUAL, "java/lang/StringBuilder",
                    "append", "(Z)Ljava/lang/StringBuilder;", false
            )
            )
            instructions.insertBefore(
                    it, MethodInsnNode(
                    INVOKEVIRTUAL, "java/lang/StringBuilder",
                    "toString", "()Ljava/lang/String;", false
            )
            )
            instructions.insertBefore(
                    it, MethodInsnNode(
                    INVOKESTATIC, "com/wallstreetcn/testmodule/KronosContextKt",
                    "show", "(Landroid/app/Application;Ljava/lang/String;)V", false
            )
            )
        }
    }
}
