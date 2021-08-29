package com.wallstreetcn.sample.viewexpose

import android.view.View

/**
 * @Author LiABao
 * @Since 2021/4/7
 */


fun View.addExposeListener(invoke: (Float) -> Unit) {
    val exposeDelegate = ExposeViewDelegate(this, object : OnExposeListener {
        override fun onExpose(exposeTime: Float) {
            invoke.invoke(exposeTime)
        }
    })
}


fun View.addExposeListener(listener: OnExposeListener?) {
    val exposeDelegate = ExposeViewDelegate(this, listener)
}