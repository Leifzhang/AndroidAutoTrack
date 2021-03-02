package com.kronos.plugin.multi.graph

data class ModuleNode(
        override val moduleName: String,
        override var taskDependencies: List<String>
) : Node {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ModuleNode

        if (moduleName != other.moduleName) return false

        return true
    }

    override fun hashCode(): Int {
        return moduleName.hashCode()
    }
}

