package com.kronos.doubletap

import com.google.auto.service.AutoService
import com.kronos.plugin.base.PluginProvider
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * @Author LiABao
 * @Since 2021/1/27
 */
//@AutoService(value = [PluginProvider::class])
class DoubleTapProvider : PluginProvider {
    override fun getPlugin(): Class<out Plugin<Project>> {
        return DoubleTapPlugin::class.java
    }

    override fun dependOn(): List<String> {
        return emptyList()
    }

}