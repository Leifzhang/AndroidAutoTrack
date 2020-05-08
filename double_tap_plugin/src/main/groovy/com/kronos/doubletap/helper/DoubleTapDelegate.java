package com.kronos.doubletap.helper;


import com.kronos.doubletap.base.AbstractAsmDelegate;
import com.kronos.doubletap.base.AsmHelper;

public class DoubleTapDelegate extends AbstractAsmDelegate {
    @Override
    public AsmHelper createHelper() {
        return new DoubleTapAsmHelper();
    }
}
