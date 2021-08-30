package com.wallstreetcn.sample.viewexpose

import android.graphics.Rect
import android.util.Log
import android.view.View
import android.view.ViewGroup

class ExposeChecker {
    private var startTime: Long = 0
    var exposeTime: Float = 0F
    var isViewCover: Boolean = false

    fun updateStartTime() {
        startTime = System.currentTimeMillis()
    }

    fun isViewExpose(view: View): Boolean {
        exposeTime = (System.currentTimeMillis() - startTime) / 1000F
        isViewCover = view.isCover()
        if (isViewCover && exposeTime > 1.5f) {
            Log.i(ExposeConstant.TAG, "viewText:${(view.hashCode())} viewCover:$isViewCover")
            return true
        }
        return false
    }
}

fun View.visibleRect(): Boolean {
    // Log.i(ExposeConstant.TAG, "visible:$partVisible")
    return getLocalVisibleRect(Rect())
}

fun View.isCover(): Boolean {
    var view = this
    val currentViewRect = Rect()
    val partVisible: Boolean = view.getLocalVisibleRect(currentViewRect)
    val totalHeightVisible =
            currentViewRect.bottom - currentViewRect.top >= view.measuredHeight
    val totalWidthVisible =
            currentViewRect.right - currentViewRect.left >= view.measuredWidth
    val totalViewVisible = partVisible && totalHeightVisible && totalWidthVisible
    if (!totalViewVisible)
        return true
    while (view.parent is ViewGroup) {
        val currentParent = view.parent as ViewGroup
        if (currentParent.visibility != View.VISIBLE) //if the parent of view is not visible,return true
            return true

        val start = view.indexOfViewInParent(currentParent)
        for (i in start + 1 until currentParent.childCount) {
            val viewRect = Rect()
            view.getGlobalVisibleRect(viewRect)
            val otherView = currentParent.getChildAt(i)
            val otherViewRect = Rect()
            otherView.getGlobalVisibleRect(otherViewRect)
            if (Rect.intersects(viewRect, otherViewRect)) {
                //if view intersects its older brother(covered),return true
                return true
            }
        }
        view = currentParent
    }
    return false
}

fun View.indexOfViewInParent(parent: ViewGroup): Int {
    var index = 0
    while (index < parent.childCount) {
        if (parent.getChildAt(index) === this) break
        index++
    }
    return index
}