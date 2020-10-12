package com.kronos.plugin.thread

import org.objectweb.asm.Opcodes

object ThreadPoolCreator {

    val poolList = mutableListOf<PoolEntity>()

    init {
        val fix = PoolEntity(
            Opcodes.INVOKESTATIC,
            "java/util/concurrent/Executors",
            "newFixedThreadPool",
            "(I)Ljava/util/concurrent/ExecutorService;",
            "getThreadPool"
        )
        poolList.add(fix)
        var single = PoolEntity(
            Opcodes.INVOKESTATIC,
            "java/util/concurrent/Executors",
            "newSingleThreadExecutor",
            "()Ljava/util/concurrent/ExecutorService;",
            "getThreadPool"
        )
        poolList.add(single)
        single = PoolEntity(
            Opcodes.INVOKESTATIC,
            "java/util/concurrent/Executors",
            "newSingleThreadExecutor",
            "(Ljava/util/concurrent/ThreadFactory;)Ljava/util/concurrent/ExecutorService;",
            "getThreadPool"
        )
        poolList.add(single)
        val work = PoolEntity(
            Opcodes.INVOKESTATIC,
            "java/util/concurrent/Executors",
            "newWorkStealingPool",
            "(I)Ljava/util/concurrent/ExecutorService;"
        )
        poolList.add(work)
        val cache = PoolEntity(
            Opcodes.INVOKESTATIC,
            "java/util/concurrent/Executors",
            "newCachedThreadPool",
            "(I)Ljava/util/concurrent/ExecutorService;",
            "getThreadPool"
        )
        poolList.add(cache)
    }
}