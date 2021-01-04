package com.kronos.doubletap

import com.android.build.api.transform.QualifiedContent
import com.android.build.gradle.internal.pipeline.TransformManager

/**
 * @Author LiABao
 * @Since 2021/1/4
 */
class DoubleTapAppTransform : DoubleTapTransform() {

    override fun getScopes(): MutableSet<in QualifiedContent.Scope>? {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    override fun getInputTypes(): Set<QualifiedContent.ContentType>? {
        return TransformManager.CONTENT_JARS
    }

}