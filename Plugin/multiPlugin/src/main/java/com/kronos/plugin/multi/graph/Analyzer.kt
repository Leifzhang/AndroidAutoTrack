package com.kronos.plugin.multi.graph

import com.kronos.plugin.base.Log
import org.gradle.internal.graph.CachingDirectedGraphWalker
import org.gradle.internal.graph.DirectedGraph
import java.util.concurrent.ConcurrentHashMap

/**
 * Use dfs find the circle, replace to Tarjan algorithm later.
 */
class Analyzer(private val libs: MutableList<ModuleNode>) {

    private val modules = ConcurrentHashMap<String, ModuleNode>()
    lateinit var bfsSortList: MutableList<ModuleNode>

    fun bfsSort() {
        bfsSortList = ModuleBfsHelper.sort(libs)
        Log.info("analyzer graphList:$bfsSortList")
    }

    fun analyze() {
        val walker = CachingDirectedGraphWalker(object : DirectedGraph<ModuleNode, ModuleNode> {
            override fun getNodeValues(node: ModuleNode, values: MutableCollection<in ModuleNode>, connectedNodes: MutableCollection<in ModuleNode>) {
                node.taskDependencies.forEach { name ->
                    modules[name]?.let {
                        connectedNodes.add(it)
                    }
                }
            }
        })

        bfsSortList.forEach {
            val nodes = arrayListOf<ModuleNode>()
            modules[it.moduleName] = it
            nodes.add(it)
            synchronized(walker) {
                walker.add(nodes)
            }
        }
        val cycles = walker.findCycles()

        check(cycles.isEmpty()) {
            var num = 1
            val sb = StringBuilder()
            sb.append("Found Cycles:\n")

            cycles.forEach { cycle ->
                sb.append("  Dependency Cycle ${num++}:\n")
                cycle.joinTo(sb, "\n") {
                    "\t" + it.toString()
                }
            }
            throw NullPointerException(sb.toString())
        }

        //return cycles
    }

}


interface Node {
    val moduleName: String
    val taskDependencies: List<String>
}
