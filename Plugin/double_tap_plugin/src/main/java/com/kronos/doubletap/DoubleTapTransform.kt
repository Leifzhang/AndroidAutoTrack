package com.kronos.doubletap

import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformException
import com.android.build.api.transform.TransformInvocation
import com.kronos.doubletap.helper.DoubleTapAsmHelper
import com.kronos.doubletap.helper.DoubleTapClassNodeHelper
import com.kronos.plugin.base.BaseTransform
import com.kronos.plugin.base.ClassUtils
import com.kronos.plugin.base.TransformCallBack
import java.io.IOException

abstract class DoubleTapTransform : Transform() {
    override fun getName(): String {
        return "DoubleTapTransform"
    }

    override fun isIncremental(): Boolean {
        return true
    }

    @Throws(TransformException::class, InterruptedException::class, IOException::class)
    override fun transform(transformInvocation: TransformInvocation) {
        val injectHelper = DoubleTapClassNodeHelper()
        val baseTransform = BaseTransform(transformInvocation, object : TransformCallBack {
            override fun process(className: String, classBytes: ByteArray?): ByteArray? {
                if (ClassUtils.checkClassName(className)) {
                    try {
                        return classBytes?.let { injectHelper.modifyClass(it) }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
                return null
            }
        })
        baseTransform.startTransform()
    }

    override fun isCacheable(): Boolean {
        return true
    }
}