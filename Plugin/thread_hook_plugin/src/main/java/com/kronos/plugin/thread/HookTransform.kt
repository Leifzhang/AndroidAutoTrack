package com.kronos.plugin.thread

import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformInvocation
import com.android.build.gradle.internal.pipeline.TransformManager
import com.kronos.plugin.base.BaseTransform
import com.kronos.plugin.base.ClassUtils
import com.kronos.plugin.base.TransformCallBack
import com.kronos.plugin.thread.visitor.ThreadAsmHelper

/**
 * @Author LiABao
 * @Since 2020/10/12
 */
class HookTransform : Transform() {
    override fun getName(): String {
        return "HookTransform"
    }

    override fun getInputTypes(): MutableSet<QualifiedContent.ContentType> {
        return mutableSetOf<QualifiedContent.ContentType>().apply {
            addAll(TransformManager.CONTENT_JARS)
        }
    }

    override fun isIncremental(): Boolean {
        return true
    }

    override fun getScopes(): MutableSet<in QualifiedContent.ScopeType> {
        return mutableSetOf<QualifiedContent.ScopeType>().apply {
            addAll(TransformManager.SCOPE_FULL_PROJECT)
        }
    }

    override fun transform(transformInvocation: TransformInvocation?) {
        val helper = ThreadAsmHelper()
        val baseTransform = BaseTransform(transformInvocation, object : TransformCallBack {
            override fun process(className: String, classBytes: ByteArray?): ByteArray? {
                return if (ClassUtils.checkClassName(className)) {
                    helper.modifyClass(classBytes)
                } else {
                    null
                }
            }
        })
        baseTransform.startTransform()
    }
}