package com.kronos.plugin.base.graph

import java.util.concurrent.ConcurrentHashMap

/**
 * Use dfs find the circle, replace to Tarjan algorithm later.
 */
class Analyzer(private val libs: List<LibraryMeta>, private val allowMiss: Boolean) {

    fun analyze() {

        // ref
        val services = ConcurrentHashMap<Pair<String, String>, Pair<String, Any>>()

        val modules = ConcurrentHashMap<String, ModuleMeta>()

        fun putService(key: Pair<String, String>, value: Pair<String, Any>) {
            services.put(key, value)?.let { preValue ->
                error(
                        "Found duplicated service (${key.first}, ${key.second})\n" +
                                "producer 1: ${value.second}\n" +
                                "producer 2: ${preValue.second}"
                )
            }
        }

        val tasks = ConcurrentHashMap<Pair<String, String>, TaskMeta>()

        fun putTask(p: Pair<String, String>, task: TaskMeta) {
            tasks.put(p, task)?.let {
                error("Found duplicated task ${task.taskName} in module ${p.first}")
            }
        }


        val walker = CachingDirectedGraphWalker(false, object : DirectedGraph<Node, Nothing> {
            override fun getNodeValues(
                    node: Node,
                    values: MutableCollection<in Nothing>,
                    connectedNodes: MutableCollection<in Node>
            ) {
                node.taskDependencies.forEach {
                    val dot = it.indexOf('.')
                    val p = if (dot < 0) {
                        node.moduleName to it
                    } else {
                        it.substring(0, dot) to it.substring(dot + 1)
                    }
                    tasks[p]?.let {
                        connectedNodes += TaskNode(p.first, it)
                    }
                            ?: if (!allowMiss) error("Task(${p.first}, ${p.second}) that $node dependsOn does not exists.")
                }
            }
        })

        libs.parallelStream()
                .flatMap {
                    it.modules.stream()
                }.forEach {
                    val nodes = arrayListOf<Node>()

                    modules.put(it.moduleName, it)?.let {
                        error("Duplicated module: ${it.moduleName}")
                    }
                    val moduleName = it.moduleName
                    it.tasks.forEach { taskMeta ->
                        taskMeta.producedServices.forEach { output ->
                            output.serviceTypes.forEach { serviceType ->
                                putService(serviceType to output.name, moduleName to taskMeta)
                            }
                        }
                        putTask(moduleName to taskMeta.taskName, taskMeta)
                        nodes += TaskNode(moduleName, taskMeta)
                    }
                    it.onCreate?.let {
                        putTask(moduleName to it.taskName, it)
                        nodes += TaskNode(moduleName, it)
                    }
                    it.onPostCreate?.let {
                        putTask(moduleName to it.taskName, it)
                        nodes += TaskNode(moduleName, it)
                    }
                    synchronized(walker) {
                        walker.add(nodes)
                    }
                }

        libs.parallelStream()
                .forEach {
                    it.consumers.forEach {
                        it.consumerDetails.forEach { consumer ->
                            val service =
                                    services[consumer.dependency.className to consumer.dependency.serviceName]
                            check(service != null || consumer.dependency.optional || allowMiss) {
                                "Can't inject ${it.consumerClassName}.${consumer.fieldOrMethodName} which correspond service (${consumer.dependency.className}, ${consumer.dependency.serviceName}) is not exists."
                            }
                        }
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
            sb.toString()
        }
    }
}

interface Node {
    val moduleName: String
    val taskDependencies: List<String>
}

fun Pair<String, Any>.toNode(): Node {
    return if (second is ServiceMeta) {
        ServiceNode(first, second as ServiceMeta)
    } else if (second is TaskMeta) {
        TaskNode(first, second as TaskMeta)
    } else {
        throw AssertionError()
    }
}

internal class ServiceNode(override val moduleName: String, val serviceMeta: ServiceMeta) :
        Node {
    override val taskDependencies: List<String>
        get() = serviceMeta.taskDependencies


    override fun toString(): String {
        return if (serviceMeta.sourceMethodName == "<init>") {
            "${serviceMeta.sourceClassName}(...)"
        } else {
            "${serviceMeta.sourceClassName}.${serviceMeta.sourceMethodName}(...)"
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ServiceNode) return false

        if (moduleName != other.moduleName) return false
        // use ref equal
        if (serviceMeta !== other.serviceMeta) return false

        return true
    }

    override fun hashCode(): Int {
        var result = moduleName.hashCode()
        result = 31 * result + serviceMeta.hashCode()
        return result
    }
}

internal class TaskNode(override val moduleName: String, val taskMeta: TaskMeta) :
        Node {
    override val taskDependencies: List<String>
        get() = taskMeta.taskDependencies


    override fun toString(): String {
        return "Task(name=${taskMeta.taskName}, module=${moduleName})"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TaskNode) return false

        if (moduleName != other.moduleName) return false
        // use ref equal
        if (taskMeta !== other.taskMeta) return false

        return true
    }

    override fun hashCode(): Int {
        var result = moduleName.hashCode()
        result = 31 * result + taskMeta.hashCode()
        return result
    }
}