package com.kronos.plugin.thread

import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformInvocation
import com.android.build.gradle.internal.pipeline.TransformManager
import com.kronos.plugin.base.BaseTransform
import com.kronos.plugin.base.ClassUtils
import com.kronos.plugin.base.TransformCallBack
import com.kronos.plugin.thread.visitor.ThreadAsmHelper
import org.gradle.api.Project

/**
 * @Author LiABao
 * @Since 2020/10/12
 */
class HookTransform(private val project: Project) : Transform() {
    override fun getName(): String {
        return "HookTransform"
    }

    override fun getInputTypes(): MutableSet<QualifiedContent.ContentType> {
        return TransformManager.CONTENT_JARS
    }

    override fun isIncremental(): Boolean {
        return true
    }

    override fun getScopes(): MutableSet<in QualifiedContent.Scope> {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    override fun transform(transformInvocation: TransformInvocation?) {
        val helper = ThreadAsmHelper()
        val baseTransform =
            BaseTransform(transformInvocation) { s: String, bytes: ByteArray ->
                if (ClassUtils.checkClassName(s)) {
                    helper.modifyClass(bytes)
                } else {
                    null
                }
            }
        baseTransform.startTransform()
    }
}