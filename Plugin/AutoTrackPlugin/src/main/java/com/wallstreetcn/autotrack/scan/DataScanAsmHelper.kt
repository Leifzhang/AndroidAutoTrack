package com.wallstreetcn.autotrack.scan

import com.kronos.plugin.base.AsmHelper
import com.kronos.plugin.base.Log
import com.wallstreetcn.autotrack.scan.DataClassManager.Companion.INSTANCE
import org.objectweb.asm.ClassReader
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.ClassNode

/**
 * @Author LiABao
 * @Since 2021/1/9
 */
class DataScanAsmHelper : AsmHelper {

    override fun modifyClass(srcClass: ByteArray?): ByteArray {
        val classNode = ClassNode(Opcodes.ASM5)
        val classReader = ClassReader(srcClass)
        //1 将读入的字节转为classNode
        classReader.accept(classNode, 0)
        if (classNode.superName == "java/lang/Object" || classNode.interfaces.contains(PARCEL)) {
            INSTANCE.datList.add(classNode.name)
        }

        return requireNotNull(srcClass)
    }

    companion object {
        const val PARCEL = "android/os/Parcelable"
    }
}