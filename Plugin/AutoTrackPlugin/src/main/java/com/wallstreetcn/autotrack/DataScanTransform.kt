package com.wallstreetcn.autotrack

import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformException
import com.android.build.api.transform.TransformInvocation
import com.android.build.gradle.internal.pipeline.TransformManager
import com.kronos.plugin.base.BaseTransform
import com.kronos.plugin.base.ClassUtils
import com.kronos.plugin.base.TransformCallBack
import com.wallstreetcn.autotrack.scan.DataScanAsmHelper
import java.io.IOException

/**
 * @Author LiABao
 * @Since 2021/1/9
 */
class DataScanTransform : Transform() {
    override fun getName(): String {
        return "DataScanTransform"
    }

    override fun getInputTypes(): Set<QualifiedContent.ContentType> {
        return TransformManager.CONTENT_JARS
    }

    override fun getScopes(): MutableSet<in QualifiedContent.Scope>? {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    override fun isIncremental(): Boolean {
        return true
    }

    @Throws(TransformException::class, InterruptedException::class, IOException::class)
    override fun transform(transformInvocation: TransformInvocation) {
        val injectHelper = DataScanAsmHelper()
        val baseTransform = BaseTransform(transformInvocation, object : TransformCallBack {
            override fun process(className: String, bytes: ByteArray?): ByteArray? {
                if (ClassUtils.checkClassName(className)) {
                    try {
                        return injectHelper.modifyClass(bytes)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                return null
            }
        })
        baseTransform.startTransform()
    }

}