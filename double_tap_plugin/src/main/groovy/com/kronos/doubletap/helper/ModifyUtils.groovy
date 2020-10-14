package com.kronos.doubletap.helper

import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import com.kronos.doubletap.helper.ClassFilterVisitor


class ModifyUtils {


    static byte[] modifyClass(byte[] srcClass) throws IOException {
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS)
        ClassVisitor methodFilterCV = new ClassFilterVisitor(classWriter)
        ClassReader cr = new ClassReader(srcClass)
        cr.accept(methodFilterCV, ClassReader.SKIP_DEBUG)
        return classWriter.toByteArray()
    }

}
