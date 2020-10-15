package com.kronos.plugin.thread

class PoolEntity(
    val code: Int,
    val owner: String,
    val name: String,
    val desc: String,
    val methodName: String = "getTHREAD_POOL_SHARE"
) {

    fun replaceDesc(): String {
        val index = desc.lastIndexOf(")")
        return desc.substring(0, index + 1) + ClassName
    }

    companion object {
        const val ClassName = "Lcom/wallstreetcn/sample/utils/TestIOThreadExecutor;"
        const val Owner = "com/wallstreetcn/sample/utils/TestIOThreadExecutor"
    }

}