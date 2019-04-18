package com.wallstreetcn.autotrack

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.wallstreetcn.autotrack.helper.ClassInjectHelper
import com.wallstreetcn.autotrack.helper.Log
import groovy.io.FileType
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.gradle.api.Project

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
                /*  def modifiedJar = null
                  if (isJarNeedModify(jarInput.file)) {
                      modifiedJar = modifyJarFile(jarInput.file, context.getTemporaryDir())
                  }
                  if (modifiedJar == null) {
                      modifiedJar = jarInput.file
                  } else {
                      saveModifiedJarForCheck(modifiedJar)
                  }*/
                FileUtils.copyFile(jarInput.file, dest)
            }

            input.directoryInputs.each { DirectoryInput directoryInput ->
                File dest = outputProvider.getContentLocation(directoryInput.name,
                        directoryInput.contentTypes, directoryInput.scopes,
                        Format.DIRECTORY)
                File dir = directoryInput.file
                if (dir) {
                    directoryInput.file.eachFileRecurse {
                        HashMap<String, File> modifyMap = new HashMap<>()
                        dir.traverse(type: FileType.FILES, nameFilter: ~/.*\.class/) {
                            File classFile ->
                                String absolutePath = classFile.absolutePath.replace(dir.absolutePath + File.separator, "")
                                String className = path2Classname(absolutePath)
                                if (checkClassName(className)) {
                                    project.logger.warn("ClassInjectHelper:" + className)
                                    ClassInjectHelper injectHelper = new ClassInjectHelper(className, classFile, context.getTemporaryDir())
                                    File modified = injectHelper.modify()
                                    if (modified != null) {
                                        //key为相对路径
                                        modifyMap.put(classFile.absolutePath.replace(dir.absolutePath, ""), modified)
                                    }
                                }


                        }
                        FileUtils.copyDirectory(directoryInput.file, dest)
                        modifyMap.entrySet().each {
                            Map.Entry<String, File> en ->
                                File target = new File(dest.absolutePath + en.getKey())
                                Log.info(target.getAbsolutePath())
                                if (target.exists()) {
                                    target.delete()
                                }
                                FileUtils.copyFile(en.getValue(), target)
                                //  saveModifiedJarForCheck(en.getValue())
                                en.getValue().delete()
                        }
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


}

