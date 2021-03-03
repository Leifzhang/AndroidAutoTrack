package com.kronos.plugin.multi.graph

import com.kronos.plugin.base.Log
import org.gradle.internal.graph.CachingDirectedGraphWalker
import java.util.concurrent.ConcurrentHashMap
import org.gradle.internal.graph.DirectedGraph
import org.gradle.model.internal.core.ModelNode

/**
 * Use dfs find the circle, replace to Tarjan algorithm later.
 */
class Analyzer(private val libs: List<ModuleNode>, private val allowMiss: Boolean) {

    private val modules = ConcurrentHashMap<String, ModuleNode>()

    fun analyze(): Set<ModuleNode> {
        val walker = CachingDirectedGraphWalker(object : DirectedGraph<ModuleNode, ModuleNode> {
            override fun getNodeValues(node: ModuleNode, values: MutableCollection<in ModuleNode>, connectedNodes: MutableCollection<in ModuleNode>) {
                //   values.add(node)
                node.taskDependencies.forEach { name ->
                    modules[name]?.let {
                        values.add(it)
                        //     Log.info("connectedNodes$connectedNodes")
                    }
                }
                values.add(node)
            }
        })

        libs.forEach {
            val nodes = arrayListOf<ModuleNode>()
            modules[it.moduleName] = it
            nodes.add(it)
            synchronized(walker) {
                walker.add(nodes)
            }
        }
        return walker.findValues()
    }

}


interface Node {
    val moduleName: String
    val taskDependencies: List<String>
}
