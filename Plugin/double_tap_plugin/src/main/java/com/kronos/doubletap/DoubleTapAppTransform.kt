package com.kronos.doubletap

import com.android.build.api.transform.QualifiedContent
import com.kronos.plugin.base.constant.TransformManager

/**
 * @Author LiABao
 * @Since 2021/1/4
 */
class DoubleTapAppTransform : DoubleTapTransform() {

    override fun getScopes(): MutableSet<QualifiedContent.ScopeType> {
        return mutableSetOf<QualifiedContent.ScopeType>().apply {
            addAll(TransformManager.SCOPE_FULL_PROJECT)
        }
    }

    override fun getInputTypes(): Set<QualifiedContent.ContentType>? {
        return TransformManager.CONTENT_JARS
    }

}