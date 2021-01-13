package com.wallstreetcn.autotrack.scan

import com.kronos.plugin.base.AsmHelper
import org.objectweb.asm.ClassReader
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.ClassNode

/**
 * @Author LiABao
 * @Since 2021/1/9
 */
class DataScanAsmHelper : AsmHelper {
    private val dataList = mutableListOf<String>()
    override fun modifyClass(srcClass: ByteArray?): ByteArray {
        val classNode = ClassNode(Opcodes.ASM5)
        val classReader = ClassReader(srcClass)
        //1 将读入的字节转为classNode
        classReader.accept(classNode, 0)

        if (classNode.superName == "java.lang.Object") {

        }

        return requireNotNull(srcClass)
    }
}