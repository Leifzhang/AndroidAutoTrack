package com.wallstreetcn.autotrack.helper

import com.kronos.plugin.base.Log
import org.objectweb.asm.Handle
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.InvokeDynamicInsnNode
import org.objectweb.asm.tree.MethodNode
import org.objectweb.asm.Opcodes.*

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
                if (it.name == "onClick" && it.desc.contains(")Landroid/view/View\$OnClickListener;")) {
                    Log.info("dynamicName:${it.name} dynamicDesc:${it.desc}")
                    val args = it.bsmArgs
                    args.forEach { arg ->
                        if (arg is Handle) {
                            val methodNode = findMethodByNameAndDesc(arg.name, arg.desc)
                            Log.info("findMethodByNameAndDesc argName:${arg.name}  argDesc:${arg.desc} " +
                                    "method:${method?.name} ")
                            if (methodNode?.access == ACC_PRIVATE or ACC_STATIC or ACC_SYNTHETIC) {
                                methodNode.let { it1 -> lambdaMethodNodes.add(it1) }
                            }
                        }
                    }
                }

            }
        }
    }
    lambdaMethodNodes.forEach {
        Log.info("lambdaName:${it.name} lambdaDesc:${it.desc} lambdaAccess:${it.access}")
    }
    return lambdaMethodNodes

}

fun ClassNode.findMethodByNameAndDesc(name: String, desc: String): MethodNode? {
    return methods?.firstOrNull {
        it.name == name && it.desc == desc
    }
}