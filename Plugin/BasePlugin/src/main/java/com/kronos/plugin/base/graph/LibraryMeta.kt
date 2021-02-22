package com.kronos.plugin.base.graph

data class LibraryMeta(
        val modules: List<ModuleMeta>,
        val consumers: List<ServiceConsumerClass>
)