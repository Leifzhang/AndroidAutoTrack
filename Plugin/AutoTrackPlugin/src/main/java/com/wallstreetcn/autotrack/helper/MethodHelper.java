package com.wallstreetcn.autotrack.helper;

import org.objectweb.asm.Opcodes;

import java.util.HashMap;

class MethodHelper {
    public final static String INJECT_CLASS_NAME = "com/wallstreetcn/sample/ToastHelper";
    public final static HashMap<String, MethodCell> sInterfaceMethods = new HashMap<>();

    static {
        sInterfaceMethods.put("onClick(Landroid/view/View;)V", new MethodCell(
                "onClick",
                "(Landroid/view/View;)V",
                "android/view/View$OnClickListener",
                "toast",
                "(Ljava/lang/Object;Landroid/view/View;Ljava/lang/Object;)V",
                1, 1,
                new int[]{Opcodes.ALOAD}));
    }
}
