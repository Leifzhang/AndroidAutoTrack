package com.wallstreetcn.autotrack

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.kronos.plugin.base.Log
import com.kronos.plugin.base.utils.filterTest
import com.kronos.plugin.base.utils.getTaskNamePrefix
import com.kronos.plugin.base.utils.getVariantManager
import org.gradle.api.Plugin
import org.gradle.api.Project

class AutoTrackPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val isApp = project.plugins.hasPlugin(AppPlugin::class.java)
        if (isApp) {
            val appExtension = project.extensions.getByType(AppExtension::class.java)
            val scanTransform = DataScanTransform()
            //    appExtension.registerTransform(scanTransform)
            appExtension.registerTransform(NewAutoTackTransform())
        }
    }
}