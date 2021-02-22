package com.kronos.plugin.multi.graph

import com.kronos.plugin.base.Log
import java.util.concurrent.ConcurrentHashMap

/**
 * Use dfs find the circle, replace to Tarjan algorithm later.
 */
class Analyzer(private val libs: List<ModuleNode>, private val allowMiss: Boolean) {

    fun analyze() {

        val modules = ConcurrentHashMap<String, ModuleNode>()


        val walker = CachingDirectedGraphWalker(false, object : DirectedGraph<Node, Node> {
            override fun getNodeValues(node: Node, values: MutableCollection<in Node>,
                                       connectedNodes: MutableCollection<in Node>) {
                values.add(node)
                node.taskDependencies.forEach { name ->
                    modules[name]?.let {
                        connectedNodes += ModuleNode(it.moduleName, it.taskDependencies)
                    }
                            ?: if (!allowMiss) error("Task(${name}) that $node dependsOn does not exists.")
                }
                Log.info("connectedNodes:$connectedNodes")
            }

        })

        libs.parallelStream().forEach {
            val nodes = arrayListOf<Node>()
            modules.put(it.moduleName, it)?.let {
                error("Duplicated module: ${it.moduleName}")
            }
            nodes += ModuleNode(it.moduleName, it.taskDependencies)

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
        }
        val values = walker.findValues()
        Log.info("graphValue:$values")
    }
}

interface Node {
    val moduleName: String
    val taskDependencies: List<String>
}
