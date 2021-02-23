package com.kronos.plugin.multi

import com.kronos.plugin.base.PluginProvider
import com.kronos.plugin.multi.graph.Analyzer
import com.kronos.plugin.multi.graph.ModuleNode
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.util.*

class MultiPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        // 菜虾版本byteX beta版本
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
        val analyzer = Analyzer(graph, true)
        val graphNodes = analyzer.analyze()
        graphNodes.forEach {
            map[it.moduleName]?.apply {
                project.plugins.apply(getPlugin())
            }

        }

    }
}