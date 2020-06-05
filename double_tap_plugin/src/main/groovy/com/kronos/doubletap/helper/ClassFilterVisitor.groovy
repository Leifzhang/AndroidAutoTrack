package com.kronos.doubletap.helper


import com.kronos.doubletap.*
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.FieldVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

class ClassFilterVisitor extends ClassVisitor {

    private String[] interfaces
    boolean visitedStaticBlock = false
    private String owner

    ClassFilterVisitor(ClassVisitor classVisitor) {
        super(Opcodes.ASM5, classVisitor)
    }

    @Override
    void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces)
        this.interfaces = interfaces
        if (interfaces != null && interfaces.length > 0) {
            for (Map.Entry<String, MethodCell> entry : MethodHelper.sInterfaceMethods.entrySet()) {
                MethodCell cell = entry.value
                if (cell != null && interfaces.contains(cell.parent)) {
                    visitedStaticBlock = true
                    this.owner = name
                    cv.visitField(Opcodes.ACC_PRIVATE + Opcodes.ACC_FINAL, "doubleTap",
                            String.format("L%s;", DoubleTabConfig.ByteCodeInjectClassName),
                            signature, null)
                }
            }
        }
    }

    @Override
    FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
        return super.visitField(access, name, descriptor, signature, value)
    }

    @Override
    MethodVisitor visitMethod(int access, String name,
                              String desc, String signature, String[] exceptions) {
        if (interfaces != null && interfaces.length > 0) {
            try {
                if (visitedStaticBlock && name == "<init>") {
                    MethodVisitor methodVisitor = cv.visitMethod(access, name, desc, signature, exceptions)
                    return new InitBlockVisitor(methodVisitor, owner)
                }
                MethodCell cell = MethodHelper.sInterfaceMethods.get(name + desc)
                if (cell != null && interfaces.contains(cell.parent)) {
                    MethodVisitor methodVisitor = cv.visitMethod(access, name, desc, signature, exceptions)
                    CheckVisitor mv = new CheckVisitor(methodVisitor, owner)
                    return mv
                }
            } catch (Exception e) {
                e.printStackTrace()
            }
        }
        return super.visitMethod(access, name, desc, signature, exceptions)
    }


}
