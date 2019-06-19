package com.wallstreetcn.autotrack

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.wallstreetcn.autotrack.helper.ClassInjectHelper
import com.wallstreetcn.autotrack.helper.ModifyUtils
import groovy.io.FileType
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.compress.utils.IOUtils
import org.apache.commons.io.FileUtils
import org.gradle.api.Project

import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry

class AutoTrackTransform extends Transform {

    Project project
    AutoTrackConfig config

    AutoTrackTransform(Project project, AutoTrackConfig config) {
        this.project = project
        this.config = config
    }

    @Override
    String getName() {
        return "auto-track"
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return true
    }

    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        project.logger.warn("start auto-track transform...")
        def inputs = transformInvocation.getInputs()
        def outputProvider = transformInvocation.outputProvider
        def context = transformInvocation.context

        inputs.each { TransformInput input ->
            input.jarInputs.each { JarInput jarInput ->
                String destName = jarInput.file.name
                /** 重名名输出文件,因为可能同名,会覆盖*/
                def hexName = DigestUtils.md5Hex(jarInput.file.absolutePath).substring(0, 8);
                if (destName.endsWith(".jar")) {
                    destName = destName.substring(0, destName.length() - 4)
                }
                /** 获得输出文件*/
                File dest = outputProvider.getContentLocation(destName + "_" + hexName, jarInput.contentTypes, jarInput.scopes, Format.JAR)
                def modifiedJar = null
                if (isJarNeedModify(jarInput.file)) {
                    modifiedJar = modifyJarFile(jarInput.file, context.getTemporaryDir())
                }
                if (modifiedJar == null) {
                    modifiedJar = jarInput.file
                }
                FileUtils.copyFile(modifiedJar, dest)
            }

            HashMap<String, ClassInjectHelper> modifyMap = new HashMap<>()
            input.directoryInputs.each {
                DirectoryInput directoryInput ->
                    File dest = outputProvider.getContentLocation(directoryInput.name,
                            directoryInput.contentTypes, directoryInput.scopes,
                            Format.DIRECTORY)
                    File dir = directoryInput.file
                    if (dir) {
                        directoryInput.file.eachFileRecurse {
                            dir.traverse(type: FileType.FILES, nameFilter: ~/.*\.class/) {
                                File classFile ->
                                    String absolutePath = classFile.absolutePath.replace(dir.absolutePath + File.separator, "")
                                    String className = path2Classname(absolutePath)
                                    if (checkClassName(className)) {
                                        ClassInjectHelper injectHelper = new ClassInjectHelper(className, classFile,
                                                context.getTemporaryDir())
                                        modifyMap.put(classFile.absolutePath.replace(dir.absolutePath, ""), injectHelper)
                                    }

                            }
                            FileUtils.copyDirectory(directoryInput.file, dest)
                        }
                        modifyMap.entrySet().each {
                            Map.Entry<String, ClassInjectHelper> en ->
                                File target = new File(dest.absolutePath + en.getKey())
                                if (target.exists()) {
                                    target.delete()
                                }
                                ClassInjectHelper injectHelper = en.getValue()
                                File modify = injectHelper.modify()
                                FileUtils.copyFile(modify, target)
                                //  saveModifiedJarForCheck(en.getValue())
                                modify.delete()
                        }
                    }
            }

        }
    }

    private static String path2Classname(String entryName) {
        return entryName.replace(File.separator, ".").replace(".class", "")
    }

    boolean checkClassName(String className) {
        String[] packageList = config.packageList
        boolean result = false
        for (int i = 0; i < packageList.size(); i++) {
            result = className.contains(packageList[i])
        }
        if (!result) {
            return false
        }
        return (!className.contains("R\$") && !className.endsWith("R")
                && !className.endsWith("BuildConfig"))
    }


     File modifyJarFile(File jarFile, File tempDir) {
        /** 设置输出到的jar */
        def hexName = DigestUtils.md5Hex(jarFile.absolutePath).substring(0, 8);
        def optJar = new File(tempDir, hexName + jarFile.name)
        JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(optJar));
        /**
         * 读取原jar
         */
        def file = new JarFile(jarFile);
        Enumeration enumeration = file.entries();
        while (enumeration.hasMoreElements()) {
            JarEntry jarEntry = (JarEntry) enumeration.nextElement();
            InputStream inputStream = file.getInputStream(jarEntry);

            String entryName = jarEntry.getName()
            String className

            ZipEntry zipEntry = new ZipEntry(entryName)

            jarOutputStream.putNextEntry(zipEntry)

            byte[] modifiedClassBytes = null
            byte[] sourceClassBytes = IOUtils.toByteArray(inputStream)
            if (entryName.endsWith(".class")) {
                className = path2Classname(entryName)
                if (checkClassName(className)) {
                    modifiedClassBytes = ModifyUtils.modifyClasses(sourceClassBytes)
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

    /**
     * 该jar文件是否包含需要修改的类
     * @param jarFile
     * @return
     */
    boolean isJarNeedModify(File jarFile) {
        boolean modified = false
        if (jarFile) {
            /**
             * 读取原jar
             */
            def file = new JarFile(jarFile)
            Enumeration enumeration = file.entries()
            while (enumeration.hasMoreElements()) {
                JarEntry jarEntry = (JarEntry) enumeration.nextElement()
                String entryName = jarEntry.getName()
                String className
                if (entryName.endsWith(".class")) {
                    className = entryName.replace("/", ".").replace(".class", "")
                    if (checkClassName(className)) {
                        modified = true
                        break
                    }
                }
            }
            file.close()
        }
        return modified
    }
}

