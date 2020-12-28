package com.wallstreetcn.autotrack;

import com.android.build.api.transform.QualifiedContent;
import com.android.build.api.transform.Transform;
import com.android.build.api.transform.TransformException;
import com.android.build.api.transform.TransformInvocation;
import com.android.build.gradle.internal.pipeline.TransformManager;
import com.kronos.plugin.base.BaseTransform;
import com.kronos.plugin.base.ClassUtils;
import com.kronos.plugin.base.TransformCallBack;
import com.wallstreetcn.autotrack.helper.AutoTrackDelegate;

import java.io.IOException;
import java.util.Set;

public class NewAutoTackTransform extends Transform {

    public NewAutoTackTransform() {
    }

    @Override
    public String getName() {
        return "NewAutoTackTransform";
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
        final AutoTrackDelegate injectHelper = new AutoTrackDelegate();
        BaseTransform baseTransform = new BaseTransform(transformInvocation, new TransformCallBack() {

            @Override
            public byte[] process(String className, byte[] bytes) {
                if (ClassUtils.checkClassName(className)) {
                    return injectHelper.transformByte(bytes);
                } else {
                    return null;
                }
            }

        });
        baseTransform.startTransform();
    }


}