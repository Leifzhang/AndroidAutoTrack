package com.wallstreetcn.autotrack

import com.android.build.gradle.internal.dependency.GenericTransformParameters
import com.android.build.gradle.internal.publishing.AndroidArtifacts
import com.kronos.plugin.base.Log
import org.gradle.api.artifacts.transform.*
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.FileSystemLocation
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.Input

import org.gradle.api.tasks.InputFiles


/**
 *
 *  @Author LiABao
 *  @Since 2021/9/17
 *
 */

abstract class TransformActionDemo : TransformAction<TransformActionDemo.Parameters> {

    override fun transform(outputs: TransformOutputs) {
        val input = getInputArtifact().get().asFile
        Log.info("TransformActionDemo: input:$input")
        if (!input.exists()) {
            input.parentFile.mkdirs()
         //   input.bytes = []
            outputs.file(getInputArtifact())
            return
        }
    }

    @Classpath
    @InputArtifact
    abstract fun getInputArtifact(): Provider<FileSystemLocation>


    interface Parameters : GenericTransformParameters {
        @Input
        fun getTargetType(): Property<AndroidArtifacts.ArtifactType>

        @Input
        fun getSharedLibSupport(): Property<Boolean>
    }

}