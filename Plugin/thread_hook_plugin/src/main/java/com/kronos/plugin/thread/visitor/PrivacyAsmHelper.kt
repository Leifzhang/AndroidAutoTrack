package com.kronos.plugin.thread.visitor

import com.kronos.plugin.base.AsmHelper
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
        val iterator: Iterator<MethodNode> = classNode.methods.iterator()
        while (iterator.hasNext()) {
            val method = iterator.next()
                method.instructions?.iterator()?.forEach {
                    if (it is MethodInsnNode && it.isPrivacy()) {
                        it.opcode = Opcodes.INVOKESTATIC
                        it.owner = "com/wallstreetcn/sample/utils/PrivacyUtils"
                        it.name = "getImei"
                        it.desc="(Landroid/telephony/TelephonyManager;)Ljava/lang/String;"
                     //   method.instructions.remove(it.previous)
                    }
            }
        }
        val classWriter = ClassWriter(ClassWriter.COMPUTE_MAXS)
        //3  将classNode转为字节数组
        classNode.accept(classWriter)
        return classWriter.toByteArray()
    }


    private fun MethodInsnNode.isPrivacy(): Boolean {
        if (owner == "android/telephony/TelephonyManager" && name == "getDeviceId") {
            return true
        }
        return false
    }
}