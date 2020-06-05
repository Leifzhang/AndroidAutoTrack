package com.kronos.doubletap.base

import com.android.build.api.transform.*
import com.kronos.doubletap.helper.Log
import groovy.io.FileType
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils

class BaseTransform {

    private TransformInvocation transformInvocation = null
    private TransformCallBack callBack = null
    public Context context = null
    private Collection<TransformInput> inputs = null
    private TransformOutputProvider outputProvider = null
    private boolean isIncremental = false

    BaseTransform(TransformInvocation transformInvocation, TransformCallBack callBack) {
        this.transformInvocation = transformInvocation
        this.callBack = callBack
        this.context = transformInvocation.context
        this.inputs = transformInvocation.getInputs()
        this.outputProvider = transformInvocation.outputProvider
        isIncremental = transformInvocation.isIncremental()
    }

    void startTransform() {
        if (!isIncremental) {
            outputProvider.deleteAll()
        }
        inputs.each { TransformInput input ->
            input.jarInputs.each { JarInput jarInput ->
                // 处理Jar
                Status status = jarInput.status
                if (isIncremental) {
                    switch (status) {
                        case Status.NOTCHANGED:
                            break
                        case Status.ADDED:
                        case Status.CHANGED:
                            foreachJar(jarInput)
                            break
                        case Status.REMOVED:
                            if (jarInput.getFile().exists()) {
                                FileUtils.forceDelete(jarInput.getFile())
                            }
                    }
                } else {
                    foreachJar(jarInput)
                }
            }
            input.directoryInputs.each { DirectoryInput directoryInput ->
                // 处理源码文件
                foreachClass(directoryInput)
            }
        }
    }


    private void foreachClass(DirectoryInput directoryInput) {
        File dest = outputProvider.getContentLocation(directoryInput.name, directoryInput.contentTypes,
                directoryInput.scopes, Format.DIRECTORY)
        Map<File, Status> map = directoryInput.changedFiles
        File dir = directoryInput.file
        if (isIncremental) {
            for (Map.Entry<File, Status> entry : map.entrySet()) {
                Status status = entry.value
                File file = entry.key
                switch (status) {
                    case Status.NOTCHANGED:
                        break
                    case Status.ADDED:
                    case Status.CHANGED:
                        modifySingleFile(dir, file, dest)
                        break
                    case Status.REMOVED:
                        if (dest.exists()) {
                            FileUtils.forceDelete(file)
                        }
                }
            }
        } else {
            changeFile(dir, dest)
        }

        Log.info("||-->结束遍历特定目录  ${dest.absolutePath}")
    }

    private void modifySingleFile(File dir, File file, File dest) {
        try {
            FileUtils.touch(dest);
        } catch (Exception ignored) {
            //maybe mkdirs fail for some strange reason, try again.
            Files.createParentDirs(dest);
        }
        File modified = callBack.processClass(dir, file, context.getTemporaryDir(), this)
        String key = file.absolutePath.replace(dir.absolutePath, "")
        File target = new File(dest.absolutePath + key)
        if (target.exists()) {
            target.delete()
        }
        FileUtils.copyFile(modified, target)
        modified.delete()
    }

    private void changeFile(File dir, File dest) {
        if (dir) {
            HashMap<String, File> modifyMap = new HashMap<>()
            dir.traverse(type: FileType.FILES, nameFilter: ~/.*\.class/) {
                File classFile ->
                    try {
                        File modified = callBack.processClass(dir, classFile, context.getTemporaryDir(), this)
                        if (modified != null) {
                            //key为相对路径
                            modifyMap.put(classFile.absolutePath.replace(dir.absolutePath, ""), modified)
                        }
                    } catch (Exception e) {

                    }
            }
            FileUtils.copyDirectory(dir, dest)
            modifyMap.entrySet().each {
                Map.Entry<String, File> en ->
                    File target = new File(dest.absolutePath + en.getKey())
                    if (target.exists()) {
                        target.delete()
                    }
                    FileUtils.copyFile(en.getValue(), target)
                    en.getValue().delete()
            }
        }
    }

    private void foreachJar(JarInput jarInput) {
        Log.info("||-->开始遍历jar  ${jarInput.file}")
        String destName = jarInput.file.name
        /** 重名名输出文件,因为可能同名,会覆盖*/
        def hexName = DigestUtils.md5Hex(jarInput.file.absolutePath).substring(0, 8);
        if (destName.endsWith(".jar")) {
            destName = destName.substring(0, destName.length() - 4)
        }

        Log.info("||-->开始遍历jar  ${hexName}  ${destName}")

        Log.info("||-->outputProvider ${outputProvider} ")
        /** 获得输出文件*/
        File dest = outputProvider.getContentLocation(destName + "_" + hexName,
                jarInput.contentTypes, jarInput.scopes, Format.JAR)
        Log.info("||-->dest ${dest} ")
        def modifiedJar = JarUtils.modifyJarFile(jarInput.file, context.getTemporaryDir(),
                callBack, this)

        Log.info("||-->modifiedJar ${modifiedJar} ")
        if (modifiedJar == null) {
            modifiedJar = jarInput.file
        }
        FileUtils.copyFile(modifiedJar, dest)
        Log.info("||-->结束遍历jar")
    }


}
