package com.kronos.doubletap

import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformException
import com.android.build.api.transform.TransformInvocation
import com.android.build.gradle.internal.pipeline.TransformManager
import com.kronos.doubletap.helper.DoubleTapDelegate
import com.kronos.doubletap.helper.Log
import com.kronos.plugin.base.BaseTransform
import com.kronos.plugin.base.ClassUtils
import com.kronos.plugin.base.TransformCallBack
import org.gradle.api.Project

class DoubleTabTransform extends Transform {

    Project project

    DoubleTabTransform(Project project) {
        this.project = project
    }

    @Override
    String getName() {
        return "DoubleTabTransform"
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_JARS
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return true
    }

    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        Log.info("transform")
        final DoubleTapDelegate injectHelper = new DoubleTapDelegate()
        BaseTransform baseTransform = new BaseTransform(transformInvocation, new TransformCallBack() {

            @Override
            byte[] process(String s, byte[] bytes, BaseTransform baseTransform) {
                if (ClassUtils.checkClassName(s)) {
                    return injectHelper.transformByte(bytes)
                } else {
                    return null
                }
            }
        })
        Log.info("startTransform")
        baseTransform.startTransform()
    }


    @Override
    boolean isCacheable() {
        return true
    }
}
