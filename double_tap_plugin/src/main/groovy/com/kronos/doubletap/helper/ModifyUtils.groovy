package com.kronos.doubletap.helper

import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter


class ModifyUtils {

    static byte[] modifyClasses(byte[] srcByteCode) {
        byte[] classBytesCode = null
        try {
            classBytesCode = modifyClass(srcByteCode)
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
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS)
        ClassVisitor methodFilterCV = new ClassFilterVisitor(classWriter)
        ClassReader cr = new ClassReader(srcClass)
        cr.accept(methodFilterCV, ClassReader.SKIP_DEBUG)
        return classWriter.toByteArray()
    }

    static byte[] modifyClass(byte[] srcClass, boolean isOnlyVisit) throws IOException {
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS)
        ClassVisitor adapter = new ClassFilterVisitor(classWriter)
        adapter.isOnlyVisit = isOnlyVisit
        ClassReader cr = new ClassReader(srcClass)
        cr.accept(adapter, 0)
        return classWriter.toByteArray()
    }
}
