package com.kronos.plugin.base

class Log {

    def static info(Object msg) {
        try {
            println "${msg}"
        } catch (Exception e) {
            e.printStackTrace()
        }
    }
}