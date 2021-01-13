package com.wallstreetcn.autotrack;

import com.android.build.api.transform.QualifiedContent;
import com.android.build.api.transform.Transform;
import com.android.build.api.transform.TransformException;
import com.android.build.api.transform.TransformInvocation;
import com.android.build.gradle.internal.pipeline.TransformManager;
import com.kronos.plugin.base.BaseTransform;
import com.kronos.plugin.base.ClassUtils;
import com.kronos.plugin.base.TransformCallBack;
import com.wallstreetcn.autotrack.scan.DataScanAsmHelper;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Set;

/**
 * @Author LiABao
 * @Since 2021/1/9
 */
public class DataScanTransform extends Transform {


    @Override
    public String getName() {
        return "DataScanTransform";
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
        final DataScanAsmHelper injectHelper = new DataScanAsmHelper();
        BaseTransform baseTransform = new BaseTransform(transformInvocation, new TransformCallBack() {

            @Override
            public byte[] process(@NotNull String className, byte[] bytes) {
                if (ClassUtils.checkClassName(className)) {
                    try {
                        return injectHelper.modifyClass(bytes);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }
        }, false);
        baseTransform.startTransform();
    }


}