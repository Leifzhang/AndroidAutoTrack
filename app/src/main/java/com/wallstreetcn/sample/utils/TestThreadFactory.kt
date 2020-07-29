package com.wallstreetcn.sample.utils

import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicInteger

class TestThreadFactory(name: String? = "") : ThreadFactory {
    private val poolNumber =
        AtomicInteger(1)
    private var group: ThreadGroup? = null
    private val threadNumber =
        AtomicInteger(1)
    private var namePrefix: String? = name

    init {
        val s = System.getSecurityManager()
        group =
            if (s != null) s.threadGroup else Thread.currentThread().threadGroup
        namePrefix += "test-pool-"+
        poolNumber.getAndIncrement() +
                "-thread-"
    }

    override fun newThread(r: Runnable?): Thread? {
        val t = Thread(
            group, r,
            namePrefix + threadNumber.getAndIncrement(),
            0
        )
        if (t.isDaemon) t.isDaemon = false
        if (t.priority != Thread.NORM_PRIORITY) t.priority = Thread.NORM_PRIORITY
        return t
    }
}