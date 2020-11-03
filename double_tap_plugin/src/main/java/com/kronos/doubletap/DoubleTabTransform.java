package com.kronos.doubletap;

import com.android.build.api.transform.QualifiedContent;
import com.android.build.api.transform.Transform;
import com.android.build.api.transform.TransformException;
import com.android.build.api.transform.TransformInvocation;
import com.android.build.gradle.internal.pipeline.TransformManager;
import com.kronos.doubletap.helper.DoubleTapDelegate;

import com.kronos.plugin.base.BaseTransform;
import com.kronos.plugin.base.ClassUtils;
import com.kronos.plugin.base.TransformCallBack;

import org.gradle.api.Project;

import java.io.IOException;
import java.util.Set;

public class DoubleTabTransform extends Transform {

    Project project;

    DoubleTabTransform(Project project) {
        this.project = project;
    }

    @Override
    public String getName() {
        return "DoubleTabTransform";
    }

    @Override
    public Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_JARS;
    }

    @Override
    public Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT;
    }

    @Override
    public boolean isIncremental() {
        return true;
    }

    @Override
    public void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        final DoubleTapDelegate injectHelper = new DoubleTapDelegate();
        BaseTransform baseTransform = new BaseTransform(project, transformInvocation, new TransformCallBack() {

            @Override
            public byte[] process(String s, byte[] bytes, BaseTransform baseTransform) {
                if (ClassUtils.checkClassName(s)) {
                    return injectHelper.transformByte(bytes);
                } else {
                    return null;
                }
            }
        });
        baseTransform.startTransform();
    }


    @Override
    public boolean isCacheable() {
        return true;
    }
}
