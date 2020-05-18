package com.kronos.plugin.base;

import java.io.File;

public interface TransformCallBack {

    byte[] processJarClass(String className, byte[] classBytes, BaseTransform transform);

    File processClass(File dir, File classFile, File tempDir, BaseTransform transform);
}
