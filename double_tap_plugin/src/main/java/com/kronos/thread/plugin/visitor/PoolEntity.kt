package com.kronos.thread.plugin.visitor

class PoolEntity(
    val code: Int,
    val owner: String,
    val name: String,
    val desc: String,
    val methodName: String = "getTHREAD_POOL_SHARE"
) {

    fun replaceDesc(): String {
        val index = desc.lastIndexOf(")")
        desc.substring(index)
        return desc + ClassName
    }

    companion object {
        const val ClassName = "Lcom/wallstreetcn/sample/utils/TestIOThreadExecutor;"
        const val Owner = "com/wallstreetcn/sample/utils/TestIOThreadExecutor"
    }

}