package com.wallstreetcn.autotrack.helper;

import com.kronos.plugin.base.AbstractAsmDelegate;
import com.kronos.plugin.base.AsmHelper;

public class AutoTrackDelegate extends AbstractAsmDelegate {
    @Override
    public AsmHelper createHelper() {
        return new AutoTrackHelper();
    }
}
