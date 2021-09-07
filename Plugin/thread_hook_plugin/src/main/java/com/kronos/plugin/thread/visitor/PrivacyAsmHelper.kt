package com.kronos.plugin.thread.visitor

import com.kronos.plugin.base.AsmHelper
import com.kronos.plugin.thread.visitor.privacy.PrivacyAsmEntity
import com.kronos.plugin.thread.visitor.privacy.PrivacyHelper
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.MethodInsnNode
import org.objectweb.asm.tree.MethodNode

/**
 * @Author LiABao
 * @Since 2021/8/9
 */
class PrivacyAsmHelper : AsmHelper {

    override fun modifyClass(srcClass: ByteArray?): ByteArray {
        val classNode = ClassNode(Opcodes.ASM5)
        val classReader = ClassReader(srcClass)
        //1 将读入的字节转为classNode
        classReader.accept(classNode, 0)
        //2 对classNode的处理逻辑
        PrivacyHelper.whiteList.let {
            val result = it.firstOrNull { name ->
                classNode.name.contains(name, true)
            }
            result
        }.apply {
            if (this == null) {
                println("filter: ${classNode.name}")
            }
        }
        PrivacyHelper.whiteList.firstOrNull {
            classNode.name.contains(it, true)
        }?.apply {
            val iterator: Iterator<MethodNode> = classNode.methods.iterator()
            // println("classNodeName: ${classNode.name}")
            while (iterator.hasNext()) {
                val method = iterator.next()
                method.instructions?.iterator()?.forEach {
                    if (it is MethodInsnNode) {
                        it.isPrivacy()?.apply {
                            it.opcode = code
                            it.owner = owner
                            it.name = name
                            it.desc = desc
                        }
                    }
                }
            }
        }
        val classWriter = ClassWriter(ClassWriter.COMPUTE_MAXS)
        //3  将classNode转为字节数组
        classNode.accept(classWriter)
        return classWriter.toByteArray()
    }


    private fun MethodInsnNode.isPrivacy(): PrivacyAsmEntity? {
        val pair = PrivacyHelper.privacyList.firstOrNull {
            val first = it.first
            first.owner == owner && first.code == opcode && first.name == name && first.desc == desc
        }
        return pair?.second

    }
}