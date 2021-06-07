package com.kronos.plugin.base

import java.io.File
import java.io.FileOutputStream

object ClassUtils {
    fun path2Classname(entryName: String): String {
        // 感谢大佬 dingshaoran
        //ClassUtils.path2Classname(className); File.separator donot match jar entryName on windows
        return entryName.replace(".class", "")
                .replace('\\', '.')
                .replace('/', '.')
    }

    fun checkClassName(className: String): Boolean {
        if (className.contains("R\$")) {
            return false
        }
        if (className.endsWith("R.class")) {
            return false
        }
        return (!className.contains("R\\$") && !className.endsWith("R")
                && !className.endsWith("BuildConfig"))
    }

    fun saveFile(mTempDir: File?, modifiedClassBytes: ByteArray?): File? {
        var modified: File? = null
        try {
            if (modifiedClassBytes != null) {
                modified = mTempDir
                if (modified!!.exists()) {
                    modified.delete()
                }
                modified.createNewFile()
                val stream = FileOutputStream(modified)
                stream.write(modifiedClassBytes)
                stream.close()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return modified
    }
}