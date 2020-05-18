package com.kronos.plugin.base


import org.apache.commons.io.IOUtils
import org.gradle.process.internal.ExecException

abstract class AbstractAsmDelegate {

    def asmHelper = createHelper()

    File beginTransform(String mClassName, File mClassFile, File mTempDir) {
        File modified = null
        try {
            byte[] sourceClassBytes = IOUtils.toByteArray(new FileInputStream(mClassFile))
            byte[] modifiedClassBytes = asmHelper.modifyClass(sourceClassBytes)
            if (modifiedClassBytes) {
                modified = new File(mTempDir, mClassName.replace('.', '') + '.class')
                if (modified.exists()) {
                    modified.delete()
                }
                modified.createNewFile()
                new FileOutputStream(modified).write(modifiedClassBytes)
            }
        } catch (Exception e) {
            e.printStackTrace()
        }
        return modified
    }

    byte[] transformByte(byte[] sourceClassBytes) {
        try {
            byte[] modifiedClassBytes = asmHelper.modifyClass(sourceClassBytes)
            return modifiedClassBytes
        } catch (ExecException e) {

        }
        return null
    }

    abstract AsmHelper createHelper()

}
