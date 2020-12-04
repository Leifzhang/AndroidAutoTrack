package com.wallstreetcn.sample.utils

import java.util.concurrent.*

class TestIOThreadExecutor private constructor() : ThreadPoolExecutor(
    MAX_PROCESS, MAX_PROCESS * 2,
    60, TimeUnit.SECONDS, LinkedBlockingDeque(20),
    TestThreadFactory(),
    DiscardPolicy()
) {


    companion object {
        @JvmField
        internal val MAX_PROCESS = Runtime.getRuntime().availableProcessors()

        @JvmStatic
        val THREAD_POOL_SHARE by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            TestIOThreadExecutor()
        }

        @JvmStatic
        fun getThreadPool(int: Int): TestIOThreadExecutor {
            return THREAD_POOL_SHARE
        }

        @JvmStatic
        fun getThreadPool(factory: ThreadFactory): TestIOThreadExecutor {
            return THREAD_POOL_SHARE
        }

        @JvmStatic
        fun getThreadPool(): TestIOThreadExecutor {
            return THREAD_POOL_SHARE
        }

    }

}