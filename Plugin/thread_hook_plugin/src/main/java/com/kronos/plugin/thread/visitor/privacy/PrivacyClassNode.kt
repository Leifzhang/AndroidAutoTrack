package com.kronos.plugin.thread.visitor.privacy

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.MethodInsnNode
import org.objectweb.asm.tree.MethodNode

/**
 * @Author LiABao
 * @Since 2021/10/5
 */
class PrivacyClassNode(private val nextVisitor: ClassVisitor) : ClassNode(Opcodes.ASM5) {
    override fun visitEnd() {
        super.visitEnd()
        PrivacyHelper.whiteList.let {
            val result = it.firstOrNull { whiteName ->
                name.contains(whiteName, true)
            }
            result
        }.apply {
            if (this == null) {
                //   println("filter: $name")
            }
        }
        PrivacyHelper.whiteList.firstOrNull {
            name.contains(it, true)
        }?.apply {
            val iterator: Iterator<MethodNode> = methods.iterator()
            while (iterator.hasNext()) {
                val method = iterator.next()
                method.instructions?.iterator()?.forEach {
                    if (it is MethodInsnNode) {
                        it.isPrivacy()?.apply {
                            println("privacy transform classNodeName: ${name@this}")
                            it.opcode = code
                            it.owner = owner
                            it.name = name
                            it.desc = desc
                        }
                    }
                }
            }
        }
        accept(nextVisitor)
    }
}


private fun MethodInsnNode.isPrivacy(): PrivacyAsmEntity? {
    val pair = PrivacyHelper.privacyList.firstOrNull {
        val first = it.first
        first.owner == owner && first.code == opcode && first.name == name && first.desc == desc
    }
    return pair?.second

}