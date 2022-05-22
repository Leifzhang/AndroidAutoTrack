package com.kronos.plugin.multi

import com.android.build.gradle.internal.plugins.BasePlugin
import com.android.build.gradle.internal.services.ProjectServices
import com.android.build.gradle.options.BooleanOption
import com.google.common.collect.ImmutableMap
import com.kronos.plugin.base.PluginProvider
import com.kronos.plugin.multi.graph.Analyzer
import com.kronos.plugin.multi.graph.ModuleNode
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.internal.provider.DefaultProvider
import org.joor.Reflect
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import java.util.*

class MultiPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        // 菜虾版本byteX beta版本

        project.plugins.withType(BasePlugin::class.java) {
            val service = it.getProjectService() ?: return@withType
            val projectOptions = service.projectOptions
            val projectOptionsReflect = Reflect.on(projectOptions)
            val optionValueReflect = Reflect.onClass(
                    "com.android.build.gradle.options.ProjectOptions\$OptionValue",
                    projectOptions.javaClass.classLoader
            )
            val defaultProvider = DefaultProvider() { false }
            val optionValueObj = optionValueReflect.create(projectOptions, BooleanOption.ENABLE_JETIFIER).get<Any>()
            Reflect.on(optionValueObj)
                    .set("valueForUseAtConfiguration", defaultProvider)
                    .set("valueForUseAtExecution", defaultProvider)
            val map = getNewMap(projectOptionsReflect, optionValueObj)
            projectOptionsReflect.set("booleanOptionValues", map)
        }
        val mapField = project.extensions.extraProperties.javaClass.getDeclaredFieldOrSuper("storage")
        mapField?.isAccessible = true
        project.extensions.extraProperties.set("com.android.build.gradle.internal.dependency.AndroidXDependencyCheck\$AndroidXEnabledJetifierDisabled_issue_reported", true)
        val providers = ServiceLoader.load(PluginProvider::class.java).toList()
        val graph = mutableListOf<ModuleNode>()
        val map = hashMapOf<String, PluginProvider>()
        providers.forEach {
            val list = it.dependOn()
            val className = it.javaClass.name
            val meta = ModuleNode(className, list)
            graph.add(meta)
            map[className] = it
        }
        val analyzer = Analyzer(graph)
        analyzer.bfsSort()
        analyzer.analyze()
        analyzer.bfsSortList.forEach {
            map[it.moduleName]?.apply {
                project.plugins.apply(getPlugin())
            }
        }

    }
    private fun getNewMap(projectOptionsReflect: Reflect, optionValueObj: Any): ImmutableMap<BooleanOption, Any> {
        val immutableMap = projectOptionsReflect
                .field("booleanOptionValues")
                .get<ImmutableMap<BooleanOption, *>>()
        val mapBuilder = ImmutableMap.builder<BooleanOption, Any>()
        immutableMap.forEach { (key, value) ->
            if (key != BooleanOption.ENABLE_JETIFIER) {
                mapBuilder.put(key, value)
            }
        }
        return mapBuilder.put(
                BooleanOption.ENABLE_JETIFIER,
                optionValueObj
        ).build()
    }

    private fun BasePlugin<*, *, *>?.getProjectService() =
            Reflect.on(this)
                    .field("projectServices")
                    .get<ProjectServices?>()

    fun noFinalField(field: Field) {
        val modifiersField = Field::class.java.getDeclaredField("modifiers")
        modifiersField.isAccessible = true
        modifiersField.setInt(field, field.modifiers and (Modifier.FINAL).inv())
    }
}