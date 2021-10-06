package com.wallstreetcn.autotrack

import com.android.build.api.variant.Component
import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.internal.dependency.AarTransform
import com.android.build.gradle.internal.dependency.GenericTransformParameters
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import com.android.build.gradle.internal.publishing.AndroidArtifacts
import com.android.build.gradle.internal.utils.setDisallowChanges
import com.android.build.gradle.options.BooleanOption
import com.kronos.plugin.base.Log
import com.kronos.plugin.base.utils.filterTest
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.transform.TransformAction
import org.gradle.api.artifacts.transform.TransformSpec
import org.gradle.api.attributes.Attribute
import org.gradle.api.internal.artifacts.ArtifactAttributes

class AutoTrackPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val isApp = project.plugins.hasPlugin(AppPlugin::class.java)
        if (isApp) {
            val appExtension = project.extensions.getByType(BaseAppModuleExtension::class.java)
            val scanTransform = DataScanTransform()
            appExtension.registerTransform(scanTransform)
            appExtension.registerTransform(NewAutoTackTransform())
        }

      //  val artifactType = Attribute.of("artifactType", String::class.java)
        //val transformed = Attribute.of("TransformActionDemo", Boolean::class.java)
        //val transformTarget = AndroidArtifacts.ArtifactType.AAR
        //BooleanOption.CONSUME_DEPENDENCIES_AS_SHARED_LIBRARIES
        /*  project.registerTransform(
                  TransformActionDemo::class.java,
                  AndroidArtifacts.ArtifactType.AAR,
                  transformTarget
          ) { params ->
              params.getTargetType().setDisallowChanges(transformTarget)
              params.getSharedLibSupport().setDisallowChanges(false)
          }*/

    }

    private fun <T : GenericTransformParameters> Project.registerTransform(
            transformClass: Class<out TransformAction<T>>,
            fromArtifactType: AndroidArtifacts.ArtifactType,
            toArtifactType: AndroidArtifacts.ArtifactType,
            parametersSetter: ((T) -> Unit)? = null
    ) {
        registerTransform(
                transformClass,
                fromArtifactType.type,
                toArtifactType.type,
                parametersSetter
        )
    }


    private fun <T : GenericTransformParameters> Project.registerTransform(
            transformClass: Class<out TransformAction<T>>,
            fromArtifactType: String,
            toArtifactType: String,
            parametersSetter: ((T) -> Unit)? = null
    ) {
        dependencies.registerTransform(
                transformClass
        ) { spec: TransformSpec<T> ->
            spec.from.attribute(ArtifactAttributes.ARTIFACT_FORMAT, fromArtifactType)
            spec.to.attribute(ArtifactAttributes.ARTIFACT_FORMAT, toArtifactType)
            spec.parameters.projectName.setDisallowChanges(name)
            parametersSetter?.let { it(spec.parameters) }
        }
    }
}