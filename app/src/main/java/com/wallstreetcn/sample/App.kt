package com.wallstreetcn.sample

import android.app.Application
import androidx.multidex.MultiDexApplication
import com.wallstreetcn.testmodule.KronosContext

/**
 * @Author LiABao
 * @Since 2021/1/4
 */
class App : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        KronosContext.app = this
    }
}