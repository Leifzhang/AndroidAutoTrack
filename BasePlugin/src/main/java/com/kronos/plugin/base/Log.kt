package com.kronos.plugin.base

import org.codehaus.groovy.runtime.InvokerHelper

object Log {
    @JvmStatic
    fun info(msg: Any) {
        try {
            println(InvokerHelper.toString(String.format("{%s}", msg.toString())))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}