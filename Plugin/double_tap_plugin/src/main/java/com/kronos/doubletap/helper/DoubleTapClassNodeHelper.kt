package com.kronos.doubletap.helper

import com.kronos.doubletap.DoubleTabConfig
import com.kronos.plugin.base.AsmHelper
import com.kronos.plugin.base.asm.lambdaHelper
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Label
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Opcodes.*
import org.objectweb.asm.tree.*
import java.io.IOException

class DoubleTapClassNodeHelper : AsmHelper {

    private val classNodeMap = hashMapOf<String, ClassNode>()

    @Throws(IOException::class)
    override fun modifyClass(srcClass: ByteArray): ByteArray {
        val classNode = ClassNode(ASM5)
        val classReader = ClassReader(srcClass)
        //1 将读入的字节转为classNode
        classReader.accept(classNode, 0)
        classNodeMap[classNode.name] = classNode
        // 判断当前类是否实现了OnClickListener接口
        val hasAnnotation = classNode.hasAnnotation()
        val className = classNode.outerClass
        val parentNode = classNodeMap[className]
        val hasKeepAnnotation = if (hasAnnotation) {
            true
        } else {
            parentNode?.hasAnnotation() ?: false
        }
        if (!hasKeepAnnotation) {
            classNode.interfaces?.forEach {
                if (it == "android/view/View\$OnClickListener") {
                    classNode.methods?.forEach { method ->
                        // 找到onClick 方法
                        if (method.name == "<init>") {
                            initFunction(classNode, method)
                        }
                        if (method.name == "onClick" && method.desc == "(Landroid/view/View;)V") {
                            insertTrack(classNode, method)
                        }
                    }
                }
            }
            classNode.lambdaHelper {
                (it.name == "onClick" && it.desc.contains(")Landroid/view/View\$OnClickListener;"))
            }.apply {
                if (isNotEmpty()) {
                    classNode.methods?.forEach { method ->
                        if (method.name == "<init>") {
                            initFunction(classNode, method)
                            return@forEach
                        }
                    }
                }
            }.forEach { method ->
                insertTrack(classNode, method)
            }
        }
        //调用Fragment的onHiddenChange方法
        val classWriter = ClassWriter(0)
        //3  将classNode转为字节数组
        classNode.accept(classWriter)
        return classWriter.toByteArray()
    }


    private fun insertLambda(node: ClassNode, method: MethodNode) {
        // 根据outClassName 获取到外部类的Node

    }

    private fun initFunction(node: ClassNode, method: MethodNode) {
        var hasDoubleTap = false
        node.fields?.forEach {
            if (it.name == "doubleTap") {
                hasDoubleTap = true
            }
        }
        if (!hasDoubleTap) {
            node.visitField(ACC_PRIVATE + ACC_FINAL, "doubleTap", String.format("L%s;",
                    DoubleTabConfig.ByteCodeInjectClassName), node.signature, null)
            val instructions = method.instructions
            method.instructions?.iterator()?.forEach {
                if ((it.opcode >= Opcodes.IRETURN && it.opcode <= Opcodes.RETURN) || it.opcode == Opcodes.ATHROW) {
                    instructions.insertBefore(it, VarInsnNode(ALOAD, 0))
                    instructions.insertBefore(it, TypeInsnNode(NEW, DoubleTabConfig.ByteCodeInjectClassName))
                    instructions.insertBefore(it, InsnNode(DUP))
                    instructions.insertBefore(it, MethodInsnNode(INVOKESPECIAL, DoubleTabConfig.ByteCodeInjectClassName,
                            "<init>", "()V", false))
                    instructions.insertBefore(it, FieldInsnNode(PUTFIELD, node.name, "doubleTap",
                            String.format("L%s;", DoubleTabConfig.ByteCodeInjectClassName)))
                }
            }
        }
    }


    private fun insertTrack(node: ClassNode, method: MethodNode) {
        // 判断方法名和方法描述
        val instructions = method.instructions
        val firstNode = instructions.first
        instructions?.insertBefore(firstNode, LabelNode(Label()))
        instructions?.insertBefore(firstNode, VarInsnNode(ALOAD, 0))
        instructions?.insertBefore(firstNode, FieldInsnNode(GETFIELD, node.name,
                "doubleTap", String.format("L%s;", DoubleTabConfig.ByteCodeInjectClassName)))
        instructions?.insertBefore(firstNode, MethodInsnNode(INVOKEVIRTUAL, DoubleTabConfig.ByteCodeInjectClassName,
                DoubleTabConfig.ByteCodeInjectFunctionName, "()Z", false))
        val labelNode = LabelNode(Label())
        instructions?.insertBefore(firstNode, JumpInsnNode(IFNE, labelNode))
        instructions?.insertBefore(firstNode, InsnNode(RETURN))
        instructions?.insertBefore(firstNode, labelNode)
    }


    // 判断Field是否包含注解
    private fun ClassNode.hasAnnotation(): Boolean {
        var hasAnnotation = false
        this.visibleAnnotations?.forEach { annotation ->
            //   Log.info("name:$name visibleAnnotations:${annotation.desc} ")
            if (annotation.desc == "Lcom/wallstreetcn/sample/adapter/Test;") {
                hasAnnotation = true
            }
        }
        this.invisibleAnnotations?.forEach { annotation ->
            //  Log.info("name:$name visibleAnnotations:${annotation.desc} ")
            if (annotation.desc == "Lcom/wallstreetcn/sample/adapter/Test;") {
                hasAnnotation = true
            }
        }
        return hasAnnotation
    }
}


