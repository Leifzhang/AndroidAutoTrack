package com.wallstreetcn.autotrack.helper

import com.kronos.plugin.base.AsmHelper
import com.kronos.plugin.base.Log
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.*
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
                    insertTrack(classNode, method, field)
                }
            }
        }
        val classWriter = ClassWriter(0)
        //3  将classNode转为字节数组
        classNode.accept(classWriter)
        return classWriter.toByteArray()
    }

    private fun insertTrack(node: ClassNode, method: MethodNode, field: FieldNode?) {
        if (method.name == "onClick" && method.desc == "(Landroid/view/View;)V") {
            Log.info("className:${node.name}  methodName:${method.name} methodDesc:${method.desc}")
            val instructions = method.instructions
            instructions?.iterator()?.forEach {
                if ((it.opcode >= Opcodes.IRETURN && it.opcode <= Opcodes.RETURN) || it.opcode == Opcodes.ATHROW) {
                    Log.info("className:${node.name}  insert")
                    instructions.insertBefore(it, VarInsnNode(Opcodes.ALOAD, 1))
                    instructions.insertBefore(it, VarInsnNode(Opcodes.ALOAD, 1))
                    if (field != null) {
                        field.apply {
                            instructions.insertBefore(it, VarInsnNode(Opcodes.ALOAD, 0))
                            instructions.insertBefore(
                                it, FieldInsnNode(
                                    Opcodes.GETFIELD, node.name, field.name, field.desc
                                )
                            )
                        }
                    } else {
                        instructions.insertBefore(it, LdcInsnNode("1234"))
                    }
                    instructions.insertBefore(
                        it, MethodInsnNode(
                            Opcodes.INVOKESTATIC,
                            "com/wallstreetcn/sample/ToastHelper",
                            "toast",
                            "(Ljava/lang/Object;Landroid/view/View;Ljava/lang/Object;)V",
                            false
                        )
                    )
                }
            }
        }

    }
}