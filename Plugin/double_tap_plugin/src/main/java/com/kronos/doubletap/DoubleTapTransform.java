package com.kronos.doubletap;

import com.android.build.api.transform.Transform;
import com.android.build.api.transform.TransformException;
import com.android.build.api.transform.TransformInvocation;
import com.kronos.doubletap.helper.DoubleTapAsmHelper;

import com.kronos.plugin.base.BaseTransform;
import com.kronos.plugin.base.ClassUtils;
import com.kronos.plugin.base.TransformCallBack;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public abstract class DoubleTapTransform extends Transform {

    @Override
    public String getName() {
        return "DoubleTapTransform";
    }


    @Override
    public boolean isIncremental() {
        return true;
    }

    @Override
    public void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        final DoubleTapAsmHelper injectHelper = new DoubleTapAsmHelper();
        BaseTransform baseTransform = new BaseTransform(transformInvocation, new TransformCallBack() {

            @Override
            public byte[] process(@NotNull String s, byte[] bytes) {
                if (ClassUtils.checkClassName(s)) {
                    try {
                        return injectHelper.modifyClass(bytes);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }
        });
        baseTransform.startTransform();
    }


    @Override
    public boolean isCacheable() {
        return true;
    }
}
