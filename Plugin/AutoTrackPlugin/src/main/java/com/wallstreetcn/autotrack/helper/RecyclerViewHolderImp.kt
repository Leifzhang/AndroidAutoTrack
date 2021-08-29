package com.wallstreetcn.autotrack.helper

import com.kronos.plugin.base.asm.methodEnd
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.*

/**
 *
 *  @Author LiABao
 *  @Since 2021/8/29
 *
 */
class RecyclerViewHolderImp(classNode: ClassNode) {

    init {
        if (isRecyclerViewHolder(classNode.superName)) {
            classNode.methods.firstOrNull {
                it.name == "<init>"
            }?.apply {
                instructions.forEach {
                    if (it.methodEnd()) {
                        instructions.insertBefore(it, VarInsnNode(Opcodes.ALOAD, 1))
                        instructions.insertBefore(it, TypeInsnNode(Opcodes.NEW,
                                "com/wallstreetcn/sample/viewexpose/AutoExposeImp"))
                        instructions.insertBefore(it, InsnNode(Opcodes.DUP))
                        instructions.insertBefore(it, VarInsnNode(Opcodes.ALOAD, 0))
                        instructions.insertBefore(it, MethodInsnNode(Opcodes.INVOKESPECIAL, "com/wallstreetcn/sample/viewexpose/AutoExposeImp",
                                "<init>", "(Landroidx/recyclerview/widget/RecyclerView\$ViewHolder;)V", false))
                        instructions.insertBefore(it, MethodInsnNode(Opcodes.INVOKESTATIC, "com/wallstreetcn/sample/viewexpose/ItemViewExtensionKt",
                                "addExposeListener", "(Landroid/view/View;Lcom/wallstreetcn/sample/viewexpose/OnExposeListener;)V", false))
                    }
                }
            }
        }
    }

    private fun isRecyclerViewHolder(superName: String?): Boolean {
        return superName == "androidx/recyclerview/widget/RecyclerView\$ViewHolder"
    }


}