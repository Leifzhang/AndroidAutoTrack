package com.kronos.plugin.thread.visitor

import com.kronos.plugin.base.AsmHelper
import com.kronos.plugin.base.Log.info
import com.kronos.plugin.thread.PoolEntity.Companion.Owner
import com.kronos.plugin.thread.visitor.ThreadPoolCreator.EXECUTORS_OWNER
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.MethodInsnNode
import org.objectweb.asm.tree.MethodNode
import java.io.IOException

/**
 * @Author LiABao
 * @Since 2020/10/14
 */
class ThreadAsmHelper : AsmHelper {
    @Throws(IOException::class)
    override fun modifyClass(srcClass: ByteArray): ByteArray {
        val classNode = ClassNode(Opcodes.ASM5)
        val classReader = ClassReader(srcClass)
        //1 将读入的字节转为classNode
        classReader.accept(classNode, 0)
        //2 对classNode的处理逻辑
        val iterator: Iterator<MethodNode> =
            classNode.methods.iterator()
        while (iterator.hasNext()) {
            val method = iterator.next()
            method.instructions?.iterator()?.forEach {
                if (it.opcode == Opcodes.INVOKESTATIC) {
                    if (it is MethodInsnNode) {
                        it.hookExecutors(classNode, method)
                    }
                }
            }
        }
        val classWriter = ClassWriter(0)
        //3  将classNode转为字节数组
        classNode.accept(classWriter)
        return classWriter.toByteArray()
    }

    private fun MethodInsnNode.hookExecutors(classNode: ClassNode, methodNode: MethodNode) {
        when (this.owner) {
            EXECUTORS_OWNER -> {
                info("owner:${this.owner}  name:${this.name} ")
                ThreadPoolCreator.poolList.forEach {
                    if (it.name == this.name && this.name == it.name && this.owner == it.owner) {
                        this.owner = Owner
                        this.name = it.methodName
                        this.desc = it.replaceDesc()
                        info("owner:${this.owner}  name:${this.name} desc:${this.desc} ")
                    }
                }

            }
        }
    }
}