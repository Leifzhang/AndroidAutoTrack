package com.wallstreetcn.autotrack.helper;


import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;


//其实我已经不用了  但是保留下历史吧  毕竟以前菜过
public class ClassFilterVisitor extends ClassVisitor {

    private String[] interfaces;
    private final String objectText = "java/lang/Object";


    ClassFilterVisitor(ClassVisitor classVisitor) {
        super(Opcodes.ASM5, classVisitor);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        this.interfaces = interfaces;
        if (!superName.equals(objectText)) {
            int index = name.lastIndexOf("/");
            index = index < 0 ? 0 : index + 1;
            String nameShort = name.substring(index);
            cv.visitField(Opcodes.ACC_PUBLIC + Opcodes.ACC_FINAL + Opcodes.ACC_STATIC,
                    "mTargetClassName", Type.getDescriptor(String.class),
                    null, nameShort);
        }

    }

    @Override
    public void visitOuterClass(String owner, String name, String desc) {
        super.visitOuterClass(owner, name, desc);
    }


    @Override
    public void visitEnd() {
        super.visitEnd();
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        FieldVisitor fieldVisitor = super.visitField(access, name, desc, signature, value);
        return new FieldAnnotationVisitor(Opcodes.ASM5, fieldVisitor, name, desc) {

            @Override
            void hasAnnotation(String fieldName, String fieldDesc) {
                this.fieldName = name;
                this.fieldDesc = desc;
            }
        };
    }

    @Override
    public MethodVisitor visitMethod(int access, String name,
                                     String desc, String signature, String[] exceptions) {
        if (interfaces != null && interfaces.length > 0) {
            try {
                MethodCell cell = MethodHelper.sInterfaceMethods.get(name + desc);
                for (String interfaceName : interfaces) {
                    if (cell != null && interfaceName.equals(cell.parent)) {
                        MethodVisitor methodVisitor = cv.visitMethod(access, name, desc, signature, exceptions);
                        return new MethodVisitor(Opcodes.ASM5, methodVisitor) {

                            @Override
                            public void visitInsn(int opcode) {
                                if ((opcode >= Opcodes.IRETURN && opcode <= Opcodes.RETURN)
                                        || opcode == Opcodes.ATHROW) {
                                    visitMethodWithLoadedParams(methodVisitor, Opcodes.INVOKESTATIC, MethodHelper.INJECT_CLASS_NAME,
                                            cell.agentName, cell.agentDesc, cell.paramsStart,
                                            cell.paramsCount, cell.opcodes);
                                }
                                methodVisitor.visitInsn(opcode);
                            }

                        };
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return super.visitMethod(access, name, desc, signature, exceptions);
    }


    void visitMethodWithLoadedParams(MethodVisitor methodVisitor, int opcode, String owner, String methodName, String methodDesc,
                                     int start, int count, int[] paramOpcodes) {
        methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);

        for (int i = start; i < start + count; i++) {
            methodVisitor.visitVarInsn(paramOpcodes[(i - start)], i);
        }
        methodVisitor.visitInsn(Opcodes.ACONST_NULL);
        methodVisitor.visitMethodInsn(opcode, owner, methodName, methodDesc, false);
    }

}
