package com.wallstreetcn.autotrack;

import com.android.build.api.transform.QualifiedContent;
import com.android.build.api.transform.Transform;
import com.android.build.api.transform.TransformException;
import com.android.build.api.transform.TransformInvocation;
import com.android.build.gradle.internal.pipeline.TransformManager;
import com.kronos.plugin.base.BaseTransform;
import com.kronos.plugin.base.ClassUtils;
import com.kronos.plugin.base.DeleteCallBack;
import com.kronos.plugin.base.TransformCallBack;
import com.wallstreetcn.autotrack.helper.AutoTrackDelegate;
import com.wallstreetcn.autotrack.helper.Log;

import org.gradle.api.Project;

import java.io.IOException;
import java.util.Set;

public class NewAutoTackTransform extends Transform {

    Project project;

    NewAutoTackTransform(Project project) {
        this.project = project;
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
        BaseTransform baseTransform = new BaseTransform(project, transformInvocation, new TransformCallBack() {

            @Override
            public byte[] process(String className, byte[] bytes, BaseTransform baseTransform) {
                Log.info("process:" + className);
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