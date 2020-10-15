package com.kronos.plugin.base;


public abstract class AbstractAsmDelegate {

    private AsmHelper asmHelper = createHelper();

    public byte[] transformByte(byte[] sourceClassBytes) {
        try {
            byte[] modifiedClassBytes = asmHelper.modifyClass(sourceClassBytes);
            return modifiedClassBytes;
        } catch (Exception e) {

        }
        return null;
    }

    public abstract AsmHelper createHelper();

}
