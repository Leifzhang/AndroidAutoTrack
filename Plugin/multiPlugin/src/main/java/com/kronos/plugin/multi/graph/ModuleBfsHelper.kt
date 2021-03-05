package com.kronos.plugin.multi.graph

import com.kronos.plugin.base.Log
import java.lang.NullPointerException

/**
 * @Author LiABao
 * @Since 2021/3/5
 */
object ModuleBfsHelper {

    fun sort(list: MutableList<ModuleNode>): MutableList<ModuleNode> {
        val taskMap: MutableMap<String, ModuleNode> = mutableMapOf()
        val taskChildMap: MutableMap<String, ArrayList<ModuleNode>> = mutableMapOf()
        val result = mutableListOf<ModuleNode>()
        // 入度为 0 的队列
        val queue = java.util.ArrayDeque<ModuleNode>()
        val taskIntegerHashMap = HashMap<String, Int>()
        // 建立每个 task 的入度关系
        list.forEach { task: ModuleNode ->
            val taskName = task.moduleName
            val size = task.taskDependencies.size
            taskIntegerHashMap[taskName] = size
            taskMap[taskName] = task
            if (size == 0) {
                queue.offer(task)
            }
        }
        // 建立每个 task 的 children 关系
        list.forEach { module: ModuleNode ->
            module.taskDependencies.forEach { taskName: String ->
                var moduleList = taskChildMap[taskName.trim()]
                if (moduleList == null) {
                    moduleList = ArrayList()
                }
                moduleList.add(module)
                taskChildMap[taskName] = moduleList
            }
        }

        taskChildMap.entries.iterator().forEach {
            Log.info("key is ${it.key}, value is ${it.value}")
        }

        // 使用 BFS 方法获得有向无环图的拓扑排序
        while (!queue.isEmpty()) {
            val anchorTask = queue.pop()
            result.add(anchorTask)
            val taskName = anchorTask.moduleName
            taskChildMap[taskName]?.forEach { // 遍历所有依赖这个顶点的顶点，移除该顶点之后，如果入度为 0，加入到改队列当中
                val key = it.moduleName
                var resultInt = taskIntegerHashMap[key] ?: 0
                resultInt--
                if (resultInt == 0) {
                    queue.offer(it)
                }
                taskIntegerHashMap[key] = resultInt
            }
        }

        return result
    }
}