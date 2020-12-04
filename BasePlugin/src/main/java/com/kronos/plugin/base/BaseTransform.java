package com.kronos.plugin.base;

import com.android.build.api.transform.Context;
import com.android.build.api.transform.DirectoryInput;
import com.android.build.api.transform.Format;
import com.android.build.api.transform.JarInput;
import com.android.build.api.transform.Status;
import com.android.build.api.transform.TransformInput;
import com.android.build.api.transform.TransformInvocation;
import com.android.build.api.transform.TransformOutputProvider;
import com.android.build.gradle.internal.tasks.Workers;
import com.android.ide.common.internal.WaitableExecutor;
import com.android.ide.common.workers.WorkerExecutorFacade;
import com.google.common.io.Files;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.gradle.api.Project;
import org.gradle.workers.WorkerExecutor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BaseTransform {

    private TransformInvocation transformInvocation = null;
    private TransformCallBack callBack = null;
    public Context context = null;
    private Collection<TransformInput> inputs = null;
    private TransformOutputProvider outputProvider = null;
    private boolean isIncremental = false;
    private DeleteCallBack deleteCallBack;
    private boolean simpleScan = false;
    public ClassNameFilter filter = null;
    private ExecutorService executor;
    private List<Callable<Void>> tasks = new ArrayList<>();

    public BaseTransform(TransformInvocation transformInvocation, TransformCallBack callBack) {
        this.transformInvocation = transformInvocation;
        this.callBack = callBack;
        this.context = transformInvocation.getContext();
        this.inputs = transformInvocation.getInputs();
        this.outputProvider = transformInvocation.getOutputProvider();
        isIncremental = transformInvocation.isIncremental();
        executor = Workers.INSTANCE.getDefaultExecutor();
    }

    public void setFilter(ClassNameFilter filter) {
        this.filter = filter;
    }

    public void openSimpleScan() {
        this.simpleScan = true;
    }

    public void setDeleteCallBack(DeleteCallBack deleteCallBack) {
        this.deleteCallBack = deleteCallBack;
    }

    public void startTransform() {
        try {
            if (!isIncremental) {
                outputProvider.deleteAll();
            }
            for (TransformInput input : inputs) {
                for (JarInput jarInput : input.getJarInputs()) {
                    Status status = jarInput.getStatus();
                    String destName = jarInput.getFile().getName();
                    /* 重名名输出文件,因为可能同名,会覆盖*/
                    String hexName = DigestUtils.md5Hex(jarInput.getFile().getAbsolutePath()).substring(0, 8);
                    if (destName.endsWith(".jar")) {
                        destName = destName.substring(0, destName.length() - 4);
                    }
                    /*获得输出文件*/
                    File dest = outputProvider.getContentLocation(destName + "_" + hexName,
                            jarInput.getContentTypes(), jarInput.getScopes(), Format.JAR);
                    if (isIncremental) {
                        switch (status) {
                            case NOTCHANGED:
                                break;
                            case ADDED:
                                foreachJar(dest, jarInput);
                                break;
                            case CHANGED:
                                diffJar(dest, jarInput);
                                break;
                            case REMOVED:
                                try {
                                    deleteScan(dest);
                                    if (dest.exists()) {
                                        FileUtils.forceDelete(dest);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                        }
                    } else {
                        foreachJar(dest, jarInput);
                    }
                }
                for (DirectoryInput directoryInput : input.getDirectoryInputs()) {
                    foreachClass(directoryInput);
                }
            }
            executor.invokeAll(tasks);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void foreachClass(DirectoryInput directoryInput) throws IOException {
        File dest = outputProvider.getContentLocation(directoryInput.getName(), directoryInput.getContentTypes(),
                directoryInput.getScopes(), Format.DIRECTORY);
        Map<File, Status> map = directoryInput.getChangedFiles();
        File dir = directoryInput.getFile();
        if (isIncremental) {
            for (Map.Entry<File, Status> entry : map.entrySet()) {
                Status status = entry.getValue();
                File file = entry.getKey();
                String destFilePath = file.getAbsolutePath().replace(dir.getAbsolutePath(), dest.getAbsolutePath());
                File destFile = new File(destFilePath);
                switch (status) {
                    case NOTCHANGED:
                        break;
                    case ADDED:
                    case CHANGED:
                        // modifySingleFile(dir, file, destFile);
                        Callable<Void> callable = () -> {
                            try {
                                FileUtils.touch(destFile);
                            } catch (Exception ignored) {
                                try {
                                    Files.createParentDirs(destFile);
                                } catch (Exception ignored1) {

                                }
                            }
                            modifySingleFile(dir, file, destFile);
                            return null;
                        };
                        tasks.add(callable);
                        executor.submit(callable);
                        break;
                    case REMOVED:
                        deleteDirectory(destFile, dest);
                        break;
                }
            }
        } else {
            changeFile(dir, dest);
        }
    }


    private void deleteDirectory(File destFile, File dest) {
        try {
            if (destFile.isDirectory()) {
                for (File classFile : com.android.utils.FileUtils.getAllFiles(destFile)) {
                    deleteSingle(classFile, dest);
                }
            } else {
                deleteSingle(destFile, dest);
            }
        } catch (Exception ignored) {
        }
        try {
            if (destFile.exists()) {
                FileUtils.forceDelete(destFile);
            }
        } catch (Exception ignored) {
        }
    }

    private void deleteSingle(File classFile, File dest) {
        try {
            if (classFile.getName().endsWith(".class")) {
                String absolutePath = classFile.getAbsolutePath().replace(dest.getAbsolutePath() +
                        File.separator, "");
                String className = ClassUtils.path2Classname(absolutePath);
                byte[] bytes = IOUtils.toByteArray(new FileInputStream(classFile));
                if (deleteCallBack != null) {
                    deleteCallBack.delete(className, bytes);
                }
            }
        } catch (Exception ignored) {

        }
    }

    private void modifySingleFile(File dir, File file, File dest) throws IOException {
        try {
            String absolutePath = file.getAbsolutePath().replace(dir.getAbsolutePath() +
                    File.separator, "");
            String className = ClassUtils.path2Classname(absolutePath);
            if (absolutePath.endsWith(".class")) {
                byte[] modifiedBytes = null;
                byte[] bytes = IOUtils.toByteArray(new FileInputStream(file));
                if (!simpleScan) {
                    modifiedBytes = process(className, bytes);
                } else {
                    modifiedBytes = process(className, null);
                }
                if (modifiedBytes == null) {
                    modifiedBytes = bytes;
                }
                ClassUtils.saveFile(dest, modifiedBytes);
            } else {
                FileUtils.copyFile(file, dest);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected byte[] process(String className, byte[] classBytes) {
        try {
            if (filter == null) {
                filter = new DefaultClassNameFilter();
            }
            if (!filter.filter(className)) {
                return callBack.process(className, classBytes);
            }
        } catch (Exception ignored) {

        }
        return null;
    }

    private void changeFile(File dir, File dest) throws IOException {
        if (dir.isDirectory()) {
            FileUtils.copyDirectory(dir, dest);
            for (File classFile : com.android.utils.FileUtils.getAllFiles(dir)) {
                if (classFile.getName().endsWith(".class")) {
                    Callable<Void> task = () -> {
                        String absolutePath = classFile.getAbsolutePath().replace(
                                dir.getAbsolutePath() + File.separator, "");
                        String className = ClassUtils.path2Classname(absolutePath);
                        if (!simpleScan) {
                            byte[] bytes = IOUtils.toByteArray(new FileInputStream(classFile));
                            byte[] modifiedBytes = process(className, bytes);
                            if (modifiedBytes != null) {
                                saveClassFile(modifiedBytes, dest, absolutePath);
                            }
                        } else {
                            process(className, null);
                        }
                        return null;
                    };
                    tasks.add(task);
                    executor.submit(task);
                }
            }
        }
    }

    private void saveClassFile(byte[] modifiedBytes, File dest, String absolutePath) throws Exception {
        File tempFile = new File(dest, "/temp/" + absolutePath);
        tempFile.mkdirs();
        File modified = ClassUtils.saveFile(tempFile, modifiedBytes);
        //key为相对路径
        File target = new File(dest, absolutePath);
        if (target.exists()) {
            target.delete();
        }
        FileUtils.copyFile(modified, target);
        tempFile.delete();
    }

    private void foreachJar(File dest, JarInput jarInput) {
        try {
            if (!simpleScan) {
                File modifiedJar = JarUtils.modifyJarFile(jarInput.getFile(), context.getTemporaryDir(),
                        callBack, this);
                FileUtils.copyFile(modifiedJar, dest);
            } else {
                File jarFile = jarInput.getFile();
                HashSet<String> classNames = JarUtils.scanJarFile(jarFile);
                for (String className : classNames) {
                    process(className, null);
                }
                FileUtils.copyFile(jarFile, dest);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void diffJar(File dest, JarInput jarInput) {
        try {
            HashSet<String> oldJarFileName = JarUtils.scanJarFile(dest);
            HashSet<String> newJarFileName = JarUtils.scanJarFile(jarInput.getFile());
            SetDiff diff = new SetDiff<>(oldJarFileName, newJarFileName);
            List<String> removeList = diff.getRemovedList();
            if (removeList.size() > 0) {
                JarUtils.deleteJarScan(dest, removeList, deleteCallBack);
            }
            foreachJar(dest, jarInput);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteScan(File dest) {
        try {
            JarUtils.deleteJarScan(dest, deleteCallBack);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
