package com.kronos.plugin.thread

import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * @Author LiABao
 * @Since 2020/10/12
 */
class ThreadHookPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val appExtension = project.extensions.getByType(
            AppExtension::class.java
        )
        appExtension.registerTransform(HookTransform(project))
    }
}