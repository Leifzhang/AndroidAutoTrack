package com.kronos.plugin.multi

import com.kronos.plugin.base.PluginProvider
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.util.*

class MultiPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        // 菜虾版本byteX beta版本
        val providers = ServiceLoader.load(PluginProvider::class.java).toList()
        providers.forEach {
            project.plugins.apply(it.getPlugin())
        }
    }
}