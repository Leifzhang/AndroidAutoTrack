package com.kronos.plugin.base.graph

import com.google.gson.annotations.SerializedName

data class ModuleMeta(
        val moduleName: String,
        val bootstrapMode: String,
        val desc: String,
        val entranceClass: String,
        val activatorClass: String,
        val onCreate: TaskMeta?,
        val onPostCreate: TaskMeta?,
        val attributes: List<AttributeMeta>,
        @SerializedName("routeMetas")
        val routes: List<RouteMeta>,
        @SerializedName("serviceMetas")
        val services: List<ServiceMeta>,
        @SerializedName("taskMetas")
        val tasks: List<TaskMeta>
)

data class ServiceDependency(
        val className: String,
        val serviceName: String,
        val optional: Boolean
)

data class RouteMeta(
        val routeName: String,
        val routeRules: List<String>,
        val routeType: String,
        val attributes: List<AttributeMeta>,
        val interceptors: List<String>,
        val launcher: String,
        val desc: String,
        val className: String,
        val exported: Boolean
)

data class AttributeMeta(val name: String, val value: String)

data class ServiceMeta(
        val serviceName: String,
        val returnType: String,
        val sourceClassName: String,
        val sourceMethodName: String,
        val serviceTypes: List<String>,
        val singleton: Boolean,
        val desc: String,
        val methodParams: List<ServiceDependency>,
        var taskDependencies: List<String>
)

data class ServiceConsumerClass(
        val consumerClassName: String,
        val superConsumerClassName: String?,
        val consumerDetails: List<ServiceConsumer>
)

data class ServiceConsumer(
        val fieldOrMethodName: String,
        val isField: Boolean,
        val dependency: ServiceDependency
)

data class TaskMeta(
        val taskName: String,
        val priority: Int,
        val threadMode: String,
        val className: String,
        val constructorParams: List<ServiceDependency>,
        var taskDependencies: List<String>,
        val producedServices: List<TaskOutputMeta>
)

data class TaskOutputMeta(
        val name: String,
        val returnType: String,
        val serviceTypes: List<String>,
        val desc: String,
        val fieldOrMethodName: String,
        val isField: Boolean
)