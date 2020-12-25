package com.kronos.doubletap.helper;

import org.objectweb.asm.Opcodes;

import java.util.HashMap;

class MethodHelper {
    public final static HashMap<String, MethodCell> sInterfaceMethods = new HashMap<>();

    static {
        sInterfaceMethods.put("onClick(Landroid/view/View;)V", new MethodCell(
                "onClick",
                "(Landroid/view/View;)V",
                "android/view/View$OnClickListener",
                "toast",
                "(Ljava/lang/Object;Landroid/view/View;Ljava/lang/Object;)V",
                1, 1, new int[]{Opcodes.ALOAD}));
        sInterfaceMethods.put("onClick(Landroid/content/DialogInterface;I)V", new MethodCell(
                "onClick",
                "(Landroid/content/DialogInterface;I)V",
                "android/content/DialogInterface$OnClickListener",
                "onClick",
                "(Ljava/lang/Object;Landroid/content/DialogInterface;I)V",
                0, 3,
                new int[]{Opcodes.ALOAD, Opcodes.ALOAD, Opcodes.ILOAD}));

    }
}
