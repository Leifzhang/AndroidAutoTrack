package com.wallstreetcn.sample

import android.os.SystemClock
import android.util.Log
import com.wallstreetcn.testmodule.KronosContext
import com.wallstreetcn.testmodule.show
import kotlin.math.abs

class DoubleTapCheck {
    private var timeCheck =
        TIME_CHECK

    constructor(int: Int) {
        timeCheck = int
    }

    constructor() : this(TIME_CHECK)

    private var downTimeTemp: Long = 0

    fun isNotDoubleTap(): Boolean {
        Log.i(
            "isNotDoubleTap",
            "isNotDoubleTap:${abs(downTimeTemp - System.currentTimeMillis()) > timeCheck}"
        )
        if (abs(downTimeTemp - System.currentTimeMillis()) > timeCheck) {
            downTimeTemp = System.currentTimeMillis()
            return true
        }
        KronosContext.requireApplication().show()
        return false
    }

    fun update() {
        downTimeTemp = SystemClock.currentThreadTimeMillis()
    }

    companion object {
        const val TIME_CHECK = 1000
    }
}

