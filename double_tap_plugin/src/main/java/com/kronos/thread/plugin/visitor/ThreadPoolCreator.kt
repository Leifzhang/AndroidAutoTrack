package com.kronos.thread.plugin.visitor

import org.objectweb.asm.Opcodes

object ThreadPoolCreator {

    val poolList = mutableListOf<PoolEntity>()

    init {
        val fix = PoolEntity(
            Opcodes.INVOKESTATIC,
            "java/util/concurrent/Executors",
            "newFixedThreadPool",
            "(I)Ljava/util/concurrent/ExecutorService;"
        )
        poolList.add(fix)
    }
}