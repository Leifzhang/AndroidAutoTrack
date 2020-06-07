package com.kronos.doubletap.helper

class Log {

    def static info(Object msg) {
        try {
            println "${msg}"
        } catch (Exception e) {
            e.printStackTrace()
        }
    }
}