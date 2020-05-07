package com.kronos.doubletap.helper;

import com.kronos.doubletap.base.AsmHelper;

import java.io.IOException;

public class DoubleTapAsmHelper implements AsmHelper {
    @Override
    public byte[] modifyClass(byte[] srcClass) throws IOException {
        return ModifyUtils.modifyClass(srcClass);
    }
}
