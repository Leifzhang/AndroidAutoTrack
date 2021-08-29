package com.kronos.plugin.base.asm

import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.AbstractInsnNode

/**
 *
 *  @Author LiABao
 *  @Since 2021/8/29
 *
 */
fun AbstractInsnNode.methodEnd(): Boolean {
    return ((this.opcode >= Opcodes.IRETURN && this.opcode <= Opcodes.RETURN) || this.opcode == Opcodes.ATHROW)
}