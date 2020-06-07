package com.kronos.plugin.base;


import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public abstract class AbstractAsmDelegate {

    private AsmHelper asmHelper = createHelper();

    public File beginTransform(String mClassName, File mClassFile, File mTempDir) {
        File modified = null;
        try {
            byte[] sourceClassBytes = IOUtils.toByteArray(new FileInputStream(mClassFile));
            byte[] modifiedClassBytes = asmHelper.modifyClass(sourceClassBytes);
            if (modifiedClassBytes != null) {
                modified = new File(mTempDir, mClassName.replace(".", "") + ".class");
                if (modified.exists()) {
                    modified.delete();
                }
                modified.createNewFile();
                new FileOutputStream(modified).write(modifiedClassBytes);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return modified;
    }

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
