package com.wallstreetcn.autotrack.helper

import com.kronos.plugin.base.AsmHelper
import com.kronos.plugin.base.Log
import org.objectweb.asm.ClassReader
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.AnnotationNode
import org.objectweb.asm.tree.ClassNode
import java.io.IOException

class AutoTrackHelper : AsmHelper {

    @Throws(IOException::class)
    override fun modifyClass(srcClass: ByteArray): ByteArray {
        val classNode = ClassNode(Opcodes.ASM5)
        val classReader = ClassReader(srcClass)
        //1 将读入的字节转为classNode
        classReader.accept(classNode, 0)
        classNode.interfaces?.forEach {
            if (it == "android/view/View\$OnClickListener") {

                val field = classNode.fields?.firstOrNull { field ->
                    var hasAnnotation = false
                    field.visibleAnnotations.forEach { annotation ->
                        if (annotation.desc == "Lcom/wallstreetcn/sample/adapter/Test;") {
                            hasAnnotation = true
                        }
                    }
                    hasAnnotation
                }
                classNode.methods?.forEach { method ->
                    if (method.name == "onClick" && method.desc == "(Landroid/view/View;)V") {
                        
                    }
                }
            }
        }
        return ModifyUtils.modifyClass(srcClass)
    }
}