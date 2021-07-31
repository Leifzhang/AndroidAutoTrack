package com.kronos.doubletap

import com.android.build.api.transform.QualifiedContent
import com.android.build.gradle.internal.pipeline.TransformManager
import com.google.common.collect.ImmutableSet

/**
 * @Author LiABao
 * @Since 2021/1/4
 */
class DoubleTapLibraryTransform : DoubleTapTransform() {

    override fun getScopes(): MutableSet<in QualifiedContent.Scope> {
        return ImmutableSet.of(
                QualifiedContent.Scope.PROJECT
        )
    }

    override fun getInputTypes(): Set<QualifiedContent.ContentType>? {
        return TransformManager.CONTENT_CLASS
    }

}