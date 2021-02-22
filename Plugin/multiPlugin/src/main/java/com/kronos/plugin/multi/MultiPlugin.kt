package com.kronos.plugin.multi

import com.kronos.plugin.base.Log
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
        providers.forEach {
            val list = it.dependOn()
            val meta = ModuleNode(it.javaClass.name, list)
            graph.add(meta)
            project.plugins.apply(it.getPlugin())
        }
        val analyzer = Analyzer(graph, true)
        analyzer.analyze()
        Log.info("list:$graph")
    }
}