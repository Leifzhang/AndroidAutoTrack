package com.wallstreetcn.autotrack.helper

import com.kronos.plugin.base.AsmHelper
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Opcodes.*
import org.objectweb.asm.tree.*
import java.io.IOException

class AutoTrackHelper : AsmHelper {

    private val classNodeMap = hashMapOf<String, ClassNode>()

    @Throws(IOException::class)
    override fun modifyClass(srcClass: ByteArray): ByteArray {
        val classNode = ClassNode(ASM5)
        val classReader = ClassReader(srcClass)
        //1 将读入的字节转为classNode
        classReader.accept(classNode, 0)
        classNodeMap[classNode.name] = classNode
        classNode.interfaces?.forEach {
            if (it == "android/view/View\$OnClickListener") {
                val field = classNode.fields?.firstOrNull { field ->
                    var hasAnnotation = false
                    field?.visibleAnnotations?.forEach { annotation ->
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
        visitFragment(classNode)
        val classWriter = ClassWriter(0)
        //3  将classNode转为字节数组
        classNode.accept(classWriter)
        return classWriter.toByteArray()
    }

    private fun visitFragment(classNode: ClassNode) {
        if (isFragment(classNode.superName)) {
            var hasHiddenChange = false
            classNode.methods?.forEach { methodNode ->
                if (methodNode.name == "onHiddenChanged" && methodNode.desc == "(Z)V" && methodNode.access == Opcodes.ACC_PUBLIC) {
                    hasHiddenChange = true
                    classNode.insert(methodNode)
                }
            }
            if (!hasHiddenChange) {
                val methodVisitor = classNode.visitMethod(
                    ACC_PUBLIC, "onHiddenChanged", "(Z)V",
                    classNode.signature, null
                )
                methodVisitor.visitCode()
                methodVisitor.visitVarInsn(ALOAD, 0);
                methodVisitor.visitVarInsn(ILOAD, 1);
                methodVisitor.visitMethodInsn(
                    INVOKESPECIAL, classNode.superName,
                    "onHiddenChanged", "(Z)V", false
                )
                methodVisitor.visitInsn(RETURN)
                methodVisitor.visitMaxs(2, 2)
                methodVisitor.visitEnd()
                classNode.methods.last()?.let { methodNode ->
                    if (methodNode.name == "onHiddenChanged" && methodNode.desc == "(Z)V" && methodNode.access == Opcodes.ACC_PUBLIC) {
                        classNode.insert(methodNode)
                    }
                }
            }
        }
    }


    private fun insertTrack(node: ClassNode, method: MethodNode, field: FieldNode?) {
        if (method.name == "onClick" && method.desc == "(Landroid/view/View;)V") {
            val className = node.outerClass
            val parentNode = classNodeMap[className]
            //      Log.info("outerClassName:${className}  parentNode:${parentNode?.name} ")
            val instructions = method.instructions
            instructions?.iterator()?.forEach {
                if ((it.opcode >= Opcodes.IRETURN && it.opcode <= Opcodes.RETURN) || it.opcode == Opcodes.ATHROW) {
                    instructions.insertBefore(it, VarInsnNode(Opcodes.ALOAD, 1))
                    instructions.insertBefore(it, VarInsnNode(Opcodes.ALOAD, 1))
                    if (field != null) {
                        field.apply {
                            instructions.insertBefore(it, VarInsnNode(Opcodes.ALOAD, 0))
                            instructions.insertBefore(
                                it,
                                FieldInsnNode(Opcodes.GETFIELD, node.name, field.name, field.desc)
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

fun isFragment(superName: String): Boolean {
    return superName == "androidx/fragment/app/Fragment" || superName == "android/support/v4/app/Fragment"
}


fun isRecyclerViewHolder(superName: String): Boolean {
    return superName == "androidx/recyclerview/widget/RecyclerView\$ViewHolder"
}