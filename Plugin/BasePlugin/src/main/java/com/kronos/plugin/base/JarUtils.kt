package com.kronos.plugin.base

import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.compress.utils.IOUtils
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry

internal object JarUtils {

    fun modifyJarFile(jarFile: File, tempDir: File?, transform: BaseTransform): File {
        /** 设置输出到的jar  */
        val hexName = DigestUtils.md5Hex(jarFile.absolutePath).substring(0, 8)
        val optJar = File(tempDir, hexName + jarFile.name)
        val jarOutputStream = JarOutputStream(FileOutputStream(optJar))
        jarOutputStream.use {
            val file = JarFile(jarFile)
            val enumeration = file.entries()
            while (enumeration.hasMoreElements()) {
                val jarEntry = enumeration.nextElement()
                val inputStream = file.getInputStream(jarEntry)
                val entryName = jarEntry.name
                val zipEntry = ZipEntry(entryName)
                jarOutputStream.putNextEntry(zipEntry)
                var modifiedClassBytes: ByteArray? = null
                val sourceClassBytes = IOUtils.toByteArray(inputStream)
                if (entryName.contains("module-info.class")) {
                    if (!entryName.contains("META-INF")) {
                        Log.info("jar file module-info:$entryName jarFileName:${jarFile.path}")
                        jarOutputStream.write(0)
                        jarOutputStream.closeEntry()
                        continue
                    }
                }
                if (entryName.endsWith(".class")) {
                    try {
                        modifiedClassBytes = transform.process(entryName, sourceClassBytes)
                    } catch (ignored: Exception) {
                    }
                }
                /**
                 * 读取原jar
                 */
                if (modifiedClassBytes == null) {
                    jarOutputStream.write(sourceClassBytes)
                } else {
                    jarOutputStream.write(modifiedClassBytes)
                }
                jarOutputStream.closeEntry()
            }
        }

        return optJar
    }

    fun scanJarFile(jarFile: File): HashSet<String> {
        val hashSet = HashSet<String>()
        val file = JarFile(jarFile)
        file.use {
            val enumeration = file.entries()
            while (enumeration.hasMoreElements()) {
                val jarEntry = enumeration.nextElement()
                val entryName = jarEntry.name
                if (entryName.contains("module-info.class")) {
                    Log.info("module-info:$entryName")
                    continue
                }
                if (entryName.endsWith(".class")) {
                    hashSet.add(entryName)
                }
            }
        }
        return hashSet
    }

    fun deleteJarScan(jarFile: File?, removeClasses: List<String>, callBack: DeleteCallBack?) {
        /**
         * 读取原jar
         */
        val file = JarFile(jarFile)
        file.use {
            val enumeration = file.entries()
            while (enumeration.hasMoreElements()) {
                val jarEntry = enumeration.nextElement()
                val entryName = jarEntry.name
                if (entryName.endsWith(".class") && removeClasses.contains(entryName)) {
                    val inputStream = file.getInputStream(jarEntry)
                    val sourceClassBytes = IOUtils.toByteArray(inputStream)
                    try {
                        callBack?.delete(entryName, sourceClassBytes)
                    } catch (ignored: Exception) {
                    }
                }
            }
        }
    }

    fun deleteJarScan(jarFile: File?, callBack: DeleteCallBack?) {
        /**
         * 读取原jar
         */
        val file = JarFile(jarFile)
        file.use {
            val enumeration = file.entries()
            while (enumeration.hasMoreElements()) {
                val jarEntry = enumeration.nextElement()
                val inputStream = file.getInputStream(jarEntry)
                val entryName = jarEntry.name
                val sourceClassBytes = IOUtils.toByteArray(inputStream)
                if (entryName.endsWith(".class")) {
                    try {
                        callBack?.delete(entryName, sourceClassBytes)
                    } catch (ignored: Exception) {
                    }
                }
            }
        }
    }
}