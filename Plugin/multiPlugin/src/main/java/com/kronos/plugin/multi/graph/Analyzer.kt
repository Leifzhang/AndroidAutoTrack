package com.kronos.plugin.multi.graph

import com.kronos.plugin.base.Log
import java.util.concurrent.ConcurrentHashMap

/**
 * Use dfs find the circle, replace to Tarjan algorithm later.
 */
class Analyzer(private val libs: List<ModuleNode>, private val allowMiss: Boolean) {

    fun analyze(): Set<ModuleNode> {

        val modules = ConcurrentHashMap<String, ModuleNode>()


        val walker = CachingDirectedGraphWalker(false, object : DirectedGraph<ModuleNode, ModuleNode> {
            override fun getNodeValues(node: ModuleNode, values: MutableCollection<in ModuleNode>, connectedNodes: MutableCollection<in ModuleNode>) {
                values.add(node)
                node.taskDependencies.forEach { name ->
                    modules[name]?.let {
                        connectedNodes += ModuleNode(it.moduleName, it.taskDependencies)
                    }
                            ?: if (!allowMiss) error("Task(${name}) that $node dependsOn does not exists.")
                }
            }
        })

        libs.parallelStream().forEach {
            val nodes = arrayListOf<ModuleNode>()
            modules.put(it.moduleName, it)?.let {
                error("Duplicated module: ${it.moduleName}")
            }
            nodes += ModuleNode(it.moduleName, it.taskDependencies)

            synchronized(walker) {
                walker.add(nodes)
            }
        }

        /*  val cycles = walker.findCycles()

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
          }*/
        val values = walker.findValues()
        return values
    }
}

interface Node {
    val moduleName: String
    val taskDependencies: List<String>
}
