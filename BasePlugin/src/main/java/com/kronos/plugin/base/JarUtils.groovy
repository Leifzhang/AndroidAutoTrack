package com.kronos.plugin.base

import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.compress.utils.IOUtils

import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry

class JarUtils {

    static File modifyJarFile(File jarFile, File tempDir, TransformCallBack callBack,
                              BaseTransform transform) {
        /** 设置输出到的jar */
        def hexName = DigestUtils.md5Hex(jarFile.absolutePath).substring(0, 8)
        def optJar = new File(tempDir, hexName + jarFile.name)
        JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(optJar))

        /**
         * 读取原jar
         */
        def file = new JarFile(jarFile);
        Enumeration enumeration = file.entries()
        while (enumeration.hasMoreElements()) {
            JarEntry jarEntry = (JarEntry) enumeration.nextElement()
            InputStream inputStream = file.getInputStream(jarEntry)

            String entryName = jarEntry.getName()

            ZipEntry zipEntry = new ZipEntry(entryName)

            jarOutputStream.putNextEntry(zipEntry)

            byte[] modifiedClassBytes = null
            byte[] sourceClassBytes = IOUtils.toByteArray(inputStream)
            if (entryName.endsWith(".class")) {
                try {
                    modifiedClassBytes = callBack.processJarClass(entryName, sourceClassBytes, transform)
                } catch (Exception e) {
                    // e.printStackTrace()
                }
            }
            if (modifiedClassBytes == null) {
                jarOutputStream.write(sourceClassBytes)
            } else {
                jarOutputStream.write(modifiedClassBytes)
            }
            jarOutputStream.closeEntry()
        }
        jarOutputStream.close()
        file.close()
        return optJar
    }
}
