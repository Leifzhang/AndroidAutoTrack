package com.kronos.plugin.thread

import com.google.auto.service.AutoService
import com.kronos.plugin.base.PluginProvider
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * @Author LiABao
 * @Since 2021/1/27
 */

@AutoService(value = [PluginProvider::class])
class ThreadHookProvider : PluginProvider {
    override fun getPlugin(): Class<out Plugin<Project>> {
        return ThreadHookPlugin::class.java
    }

    override fun dependOn(): List<String> {
        return emptyList()
    }
}