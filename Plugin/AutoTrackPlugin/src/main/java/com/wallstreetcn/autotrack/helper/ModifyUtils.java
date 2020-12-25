package com.wallstreetcn.autotrack.helper;


import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

import java.io.IOException;


class ModifyUtils {

    public static byte[] modifyClass(byte[] srcClass) throws IOException {
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        ClassVisitor methodFilterCV = new ClassFilterVisitor(classWriter);
        ClassReader cr = new ClassReader(srcClass);
        cr.accept(methodFilterCV, ClassReader.SKIP_DEBUG);
        return classWriter.toByteArray();
    }


    private static boolean instanceOfFragment(String superName) {
        return superName.equals("android/app/Fragment") || superName.equals("android/support/v4/app/Fragment");
    }


    static boolean isRecyclerViewHolder(String superName) {
        return superName.equals("androidx/recyclerview/widget/RecyclerView$ViewHolder");
    }
}
