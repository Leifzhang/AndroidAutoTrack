package com.wallstreetcn.testmodule

import android.app.Application
import android.widget.Toast

/**
 * @Author LiABao
 * @Since 2021/1/4
 */
object KronosContext {
    var app: Application? = null


    fun requireApplication(): Application {
        return requireNotNull(app)
    }
}

fun Application.show() {
    Toast.makeText(this, "请勿双击", Toast.LENGTH_SHORT).show()
}

fun Application.show(text: String) {
    Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
}