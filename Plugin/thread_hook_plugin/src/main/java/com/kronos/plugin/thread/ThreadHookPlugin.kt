package com.kronos.plugin.thread

import com.android.build.api.artifact.SingleArtifact
import com.android.build.api.instrumentation.FramesComputationMode
import com.android.build.api.instrumentation.InstrumentationScope
import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.gradle.AppExtension
import com.android.build.gradle.internal.crash.afterEvaluate
import com.kronos.plugin.thread.task.ManifestSampleTask
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
        appExtension.registerTransform(HookTransform())
        val androidComponents = project.extensions.getByType(AndroidComponentsExtension::class.java)
        androidComponents.onVariants { variant ->
            //  artifacts 简单使用
            variant.flavorName
            val taskProvider = project.tasks.register(
                    "manifestCopy${variant.name}Task",
                    ManifestSampleTask::class.java
            )
            variant.artifacts.use(taskProvider).wiredWithFiles(
                    ManifestSampleTask::mergedManifest,
                    ManifestSampleTask::outputManifest
            ).toTransform(SingleArtifact.MERGED_MANIFEST)
            variant.transformClassesWith(PrivacyClassVisitorFactory::class.java,
                    InstrumentationScope.ALL) {}
            variant.setAsmFramesComputationMode(FramesComputationMode.COPY_FRAMES)
        }
    }
}