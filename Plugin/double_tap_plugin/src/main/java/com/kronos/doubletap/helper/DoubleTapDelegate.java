package com.kronos.doubletap.helper;


import com.kronos.plugin.base.AbstractAsmDelegate;
import com.kronos.plugin.base.AsmHelper;

public class DoubleTapDelegate extends AbstractAsmDelegate {
    @Override
    public AsmHelper createHelper() {
        return new DoubleTapAsmHelper();
    }
}
