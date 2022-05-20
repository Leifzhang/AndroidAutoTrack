package com.kronos.plugin.multi

import com.android.build.gradle.internal.plugins.BasePlugin
import com.android.build.gradle.internal.services.ProjectServices
import com.android.build.gradle.options.BooleanOption
import com.kronos.plugin.base.PluginProvider
import com.kronos.plugin.multi.graph.Analyzer
import com.kronos.plugin.multi.graph.ModuleNode
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.util.*

class MultiPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        // 菜虾版本byteX beta版本

        project.plugins.withType(BasePlugin::class.java) {
            val serviceField = it.javaClass.getDeclaredFieldOrSuper("projectServices")
            serviceField?.isAccessible = true
            val service = serviceField?.get(it) as ProjectServices?
            service?.apply {
                val options = service.projectOptions
                projectOptions.get(BooleanOption.USE_ANDROID_X)
            }
        }

        val providers = ServiceLoader.load(PluginProvider::class.java).toList()
        val graph = mutableListOf<ModuleNode>()
        val map = hashMapOf<String, PluginProvider>()
        providers.forEach {
            val list = it.dependOn()
            val className = it.javaClass.name
            val meta = ModuleNode(className, list)
            graph.add(meta)
            map[className] = it
        }
        val analyzer = Analyzer(graph)
        analyzer.bfsSort()
        analyzer.analyze()
        analyzer.bfsSortList.forEach {
            map[it.moduleName]?.apply {
                project.plugins.apply(getPlugin())
            }
        }

    }
}