package com.wallstreetcn.autotrack.helper;

import com.kronos.plugin.base.AsmHelper;

import java.io.IOException;

public class AutoTrackHelper implements AsmHelper {
    @Override
    public byte[] modifyClass(byte[] srcClass) throws IOException {
        return ModifyUtils.modifyClass(srcClass);
    }
}
