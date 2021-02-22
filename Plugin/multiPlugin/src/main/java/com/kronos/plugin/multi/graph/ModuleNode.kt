package com.kronos.plugin.multi.graph

data class ModuleNode(
        override val moduleName: String,
        override var taskDependencies: List<String>
) : Node {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ModuleNode) return false

        if (moduleName != other.moduleName) return false
        if (taskDependencies != other.taskDependencies) return false

        return true
    }

    override fun hashCode(): Int {
        var result = moduleName.hashCode()
        result = 31 * result + taskDependencies.hashCode()
        return result
    }
}

