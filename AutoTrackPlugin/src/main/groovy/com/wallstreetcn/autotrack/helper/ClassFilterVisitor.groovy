package com.wallstreetcn.autotrack.helper;


import org.objectweb.asm.*


class ClassFilterVisitor extends ClassVisitor {
    private String superName
    private String[] interfaces
    private String parentName
    private String outerName
    private String outerOwner
    private final String objectText = "java/lang/Object"
    private String fieldName, fieldDesc, fieldOwner


    ClassFilterVisitor(ClassVisitor classVisitor) {
        super(Opcodes.ASM5, classVisitor)
    }

    @Override
    void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces)
        this.superName = superName
        this.outerName = name
        this.outerOwner = name
        this.fieldOwner = name
        this.interfaces = interfaces
        // Log.info("parentName:" + name + "   superName:" + superName + "    interfaces:" + interfaces)
        if (!superName.equals(objectText)) {
            int index = name.lastIndexOf("/")
            index = index < 0 ? 0 : index
            String nameShort = name.substring(index)
            cv.visitField(Opcodes.ACC_PUBLIC + Opcodes.ACC_FINAL + Opcodes.ACC_STATIC,
                    "mTargetClassName", Type.getDescriptor(String.class),
                    null, nameShort)
        }

    }

    @Override
    void visitOuterClass(String owner, String name, String desc) {
        super.visitOuterClass(owner, name, desc)
        this.parentName = "L" + owner + ";"
    }

    @Override
    void visitInnerClass(String name, String outerName, String innerName, int access) {
        super.visitInnerClass(name, outerName, innerName, access)
       // this.fieldOwner += innerName
    }


    @Override
    void visitEnd() {
        super.visitEnd()
    }

    @Override
    FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        FieldVisitor fieldVisitor = super.visitField(access, name, desc, signature, value)
        return new FieldAnnotationVisitor(Opcodes.ASM5, fieldVisitor, name, desc) {

            @Override
            void hasAnnotation(String fieldName, String fieldDesc) {
                this.fieldName = name
                this.fieldDesc = desc
            }
        }
    }

    @Override
    MethodVisitor visitMethod(int access, String name,
                              String desc, String signature, String[] exceptions) {
        // Log.info("* visitMethod *" + " , " + access + " , " + name + " , " + desc + " , " + signature + " , " + exceptions)
        if (interfaces != null && interfaces.length > 0) {
            try {
                MethodCell cell = MethodHelper.sInterfaceMethods.get(name + desc)
                if (cell != null && interfaces.contains(cell.parent)) {
                    MethodVisitor methodVisitor = cv.visitMethod(access, name, desc, signature, exceptions)
                    MethodVisitor mv = new MethodVisitor(Opcodes.ASM5, methodVisitor) {

                        @Override
                        void visitCode() {
                            super.visitCode()
                            /*   methodVisitor.visitMethodInsn(Opcodes.INVOKESTATIC, "java/ lang / System ", " currentTimeMillis ", "() J ", false)
                               methodVisitor.visitVarInsn(Opcodes.LSTORE, 1)*/
                        }

                        @Override
                        void visitInsn(int opcode) {
                            if ((opcode >= Opcodes.IRETURN && opcode <= Opcodes.RETURN)
                                    || opcode == Opcodes.ATHROW) {
                                /*   methodVisitor.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false);
                                   methodVisitor.visitVarInsn(Opcodes.LSTORE, 3);
                                   methodVisitor.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
                                   methodVisitor.visitTypeInsn(Opcodes.NEW, "java/lang/StringBuilder");
                                   methodVisitor.visitInsn(Opcodes.DUP);
                                   methodVisitor.visitLdcInsn("MyTimeLog now use time====");
                                   methodVisitor.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "(Ljava/lang/String;)V", false);
                                   methodVisitor.visitVarInsn(Opcodes.LLOAD, 3);
                                   methodVisitor.visitVarInsn(Opcodes.LLOAD, 1);
                                   methodVisitor.visitInsn(Opcodes.LSUB);
                                   methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(J)Ljava/lang/StringBuilder;", false);
                                   methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
                                   methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
   */
                                visitMethodWithLoadedParams(methodVisitor, Opcodes.INVOKESTATIC, MethodHelper.INJECT_CLASS_NAME,
                                        cell.agentName, cell.agentDesc, cell.paramsStart,
                                        cell.paramsCount, cell.opcodes)
                            }
                            methodVisitor.visitInsn(opcode)
                        }

                    }

                    return mv
                }
            } catch (Exception e) {
                e.printStackTrace()
            }
        }
        return super.visitMethod(access, name, desc, signature, exceptions)
    }

/**
 *
 * @param opcode
 *            the opcode of the type instruction to be visited. This opcode
 *            is either INVOKEVIRTUAL, INVOKESPECIAL, INVOKESTATIC or
 *            INVOKEINTERFACE.
 * @param owner
 *            the internal parentName of the method's owner class (see
 * {@link Type#getInternalName() getInternalName}).
 * @param parentName
 *            the method's parentName.
 * @param desc
 *            the method's descriptor (see {@link Type Type}).
 * @param start 方法参数起始索引（ 0：this，1+：普通参数 ）
 *
 * @param count 方法参数个数
 *
 * @param paramOpcodes 参数类型对应的ASM指令
 *
 */
    void visitMethodWithLoadedParams(MethodVisitor methodVisitor, int opcode, String owner, String methodName, String methodDesc,
                                     int start, int count, List<Integer> paramOpcodes) {
        //  Log.info("outerOwner:" + outerOwner + "  outerDesc:" + parentName)
        methodVisitor.visitVarInsn(Opcodes.ALOAD, 0)
        //  Log.info("visitVarInsn:")
        if (parentName != null && outerOwner != null) {
            methodVisitor.visitFieldInsn(Opcodes.GETFIELD, outerOwner, "this\$0", parentName)
            //     Log.info("visitFieldInsn:")
        }
        for (int i = start; i < start + count; i++) {
            methodVisitor.visitVarInsn(paramOpcodes[i - start], i)
            //    Log.info("method visitFieldInsn:")
        }
        if (fieldDesc != null && fieldName != null) {
            methodVisitor.visitVarInsn(Opcodes.ALOAD, 0)
            Log.info("AnnotationVisitor fieldOwner:" + fieldOwner)
            methodVisitor.visitFieldInsn(Opcodes.GETFIELD, fieldOwner, fieldName, fieldDesc)
            // methodVisitor.visitInsn(Opcodes.ACONST_NULL)
        } else {
            methodVisitor.visitInsn(Opcodes.ACONST_NULL)
        }
        methodVisitor.visitMethodInsn(opcode, owner, methodName, methodDesc, false)
    }

}
