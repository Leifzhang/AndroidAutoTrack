package com.kronos.plugin.base.utils

import com.kronos.plugin.base.Log
import org.apache.commons.io.FileUtils
import java.io.File

/**
 * @Author LiABao
 * @Since 2021/7/21
 */

fun copyIfLegal(srcFile: File?, destFile: File) {
    if (srcFile?.name?.contains("module-info") != true) {
        try {
            srcFile?.apply {
                FileUtils.copyFile(srcFile, destFile)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    } else {
        Log.info("copyIfLegal module-info:" + srcFile.name)
    }
}
