package com.kronos.plugin.thread.visitor;

import com.kronos.plugin.base.AbstractAsmDelegate;
import com.kronos.plugin.base.AsmHelper;

/**
 * @Author LiABao
 * @Since 2020/10/14
 */

public class ThreadHookDelegate extends AbstractAsmDelegate {

    @Override
    public AsmHelper createHelper() {
        return new ThreadAsmHelper();
    }
}
