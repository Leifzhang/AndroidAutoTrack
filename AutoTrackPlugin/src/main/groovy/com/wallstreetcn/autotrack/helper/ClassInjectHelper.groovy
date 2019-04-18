package com.wallstreetcn.autotrack.helper


import org.apache.commons.io.IOUtils

class ClassInjectHelper {
    def mClassName
    def mTempDir, mClassFile

    ClassInjectHelper(String className, File classFile, File temp) {
        mClassName = className
        mClassFile = classFile
        mTempDir = temp
    }

    def modify() {
        File modified = null
        try {
            byte[] sourceClassBytes = IOUtils.toByteArray(new FileInputStream(mClassFile))
            byte[] modifiedClassBytes = ModifyUtils.modifyClasses(mClassName, sourceClassBytes)
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

}

