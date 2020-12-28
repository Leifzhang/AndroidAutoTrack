package com.kronos.doubletap.helper;

import com.kronos.doubletap.DoubleTabConfig;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.Map;

class ClassFilterVisitor extends ClassVisitor {

    private String[] interfaces;
    boolean visitedStaticBlock = false;
    private String owner;

    ClassFilterVisitor(ClassVisitor classVisitor) {
        super(Opcodes.ASM5, classVisitor);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        this.interfaces = interfaces;
        if (interfaces != null && interfaces.length > 0) {
            for (Map.Entry<String, MethodCell> entry : MethodHelper.sInterfaceMethods.entrySet()) {
                MethodCell cell = entry.getValue();
                for (String anInterface : interfaces) {
                    if (anInterface.equals(cell.parent)) {
                        visitedStaticBlock = true;
                        this.owner = name;
                        cv.visitField(Opcodes.ACC_PRIVATE + Opcodes.ACC_FINAL, "doubleTap",
                                String.format("L%s;", DoubleTabConfig.ByteCodeInjectClassName),
                                signature, null);
                    }
                }
            }
        }
    }


    @Override
    public MethodVisitor visitMethod(int access, String name,
                                     String desc, String signature, String[] exceptions) {
        if (interfaces != null && interfaces.length > 0) {
            try {
                if (visitedStaticBlock && name.equals("<init>")) {
                    MethodVisitor methodVisitor = cv.visitMethod(access, name, desc, signature, exceptions);
                    return new InitBlockVisitor(methodVisitor, owner);
                }
                MethodCell cell = MethodHelper.sInterfaceMethods.get(name + desc);
                if (cell != null) {
                    for (String anInterface : interfaces) {
                        if (anInterface.equals(cell.parent)) {
                            MethodVisitor methodVisitor = cv.visitMethod(access, name, desc, signature, exceptions);
                            return new CheckVisitor(methodVisitor, owner);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return super.visitMethod(access, name, desc, signature, exceptions);
    }


}
