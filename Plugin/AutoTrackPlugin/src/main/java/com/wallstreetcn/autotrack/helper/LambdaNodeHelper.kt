package com.wallstreetcn.autotrack.helper

import com.kronos.plugin.base.Log
import org.objectweb.asm.Handle
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.InvokeDynamicInsnNode
import org.objectweb.asm.tree.MethodNode

/**
 *
 *  @Author LiABao
 *  @Since 2021/1/13
 *
 */

fun ClassNode.lambdaHelper(): MutableList<MethodNode> {
    val lambdaMethodNodes = mutableListOf<MethodNode>()
    methods?.forEach { method ->
        method.instructions.iterator()?.forEach {
            if (it is InvokeDynamicInsnNode) {
                if (it.name == "onClick" && it.desc == "()Landroid/view/View\$OnClickListener;") {
                    Log.info("InvokeDynamicInsnNode lambdaHelper methodName:${method.name}  className:$name")
                    val args = it.bsmArgs
                    args.forEach { arg ->
                        if (arg is Handle) {
                            val methodNode = findMethodByNameAndDesc(arg.name, arg.desc)
                            methodNode?.let { it1 -> lambdaMethodNodes.add(it1) }
                        }
                    }
                }

            }
        }
    }
    return lambdaMethodNodes

}

fun ClassNode.findMethodByNameAndDesc(name: String, desc: String): MethodNode? {
    return methods?.firstOrNull {
        it.name == name && it.desc == desc
    }
}