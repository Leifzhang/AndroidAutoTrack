package com.wallstreetcn.sample.viewexpose

import android.view.View
import android.view.ViewTreeObserver

/**
 * @Author LiABao
 * @Since 2021/4/7
 */
class ExposeViewDelegate(private val view: View, private val mListener: OnExposeListener?) :
        View.OnAttachStateChangeListener, ViewTreeObserver.OnWindowFocusChangeListener {

    private val exposeChecker by lazy {
        ExposeChecker()
    }

    init {
        view.viewTreeObserver.addOnWindowFocusChangeListener(this)
        view.addOnAttachStateChangeListener(this)
    }

    override fun onViewAttachedToWindow(v: View?) {
        exposeChecker.updateStartTime()
    }

    override fun onViewDetachedFromWindow(v: View?) {
        onExpose()
        // exposeChecker.updateStartTime()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        if (hasFocus) {
            exposeChecker.updateStartTime()
        } else {
            onExpose()
        }
    }


    private fun onExpose() {
        if (exposeChecker.isViewExpose(view)) {
            mListener?.onExpose(exposeChecker.exposeTime)
        }
    }

}