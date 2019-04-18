package com.wallstreetcn.autotrack.helper;

import org.objectweb.asm.*


class ModifyUtils {

    static byte[] modifyClasses(String className, byte[] srcByteCode) {
        byte[] classBytesCode = null
        try {
            Log.info("====start modifying ${className}====");
            classBytesCode = modifyClass(srcByteCode);
            Log.info("====revisit modified ${className}====");
            //  onlyVisitClassMethod(classBytesCode);
            Log.info("====finish modifying ${className}====");
            return classBytesCode
        } catch (Exception e) {
            e.printStackTrace()
        }
        if (classBytesCode == null) {
            classBytesCode = srcByteCode
        }
        return classBytesCode
    }

    static byte[] modifyClass(byte[] srcClass) throws IOException {
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        ClassVisitor methodFilterCV = new ClassFilterVisitor(classWriter);
        ClassReader cr = new ClassReader(srcClass)
        cr.accept(methodFilterCV, ClassReader.SKIP_DEBUG)
        return classWriter.toByteArray()
    }

    static byte[] modifyClass(byte[] srcClass, Map<String, String> modifyMap, boolean isOnlyVisit) throws IOException {
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS)
        ClassVisitor adapter = new ClassFilterVisitor(classWriter)
        adapter.isOnlyVisit = isOnlyVisit
        ClassReader cr = new ClassReader(srcClass)
        cr.accept(adapter, 0)
        return classWriter.toByteArray()
    }

    private static boolean instanceOfFragment(String superName) {
        return superName.equals('android/app/Fragment') || superName.equals('android/support/v4/app/Fragment')
    }


    static boolean isRecyclerViewHolder(String superName) {
        return superName.equals('androidx/recyclerview/widget/RecyclerView$ViewHolder')
    }
}
