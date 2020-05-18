package com.kronos.plugin.base;

import java.io.File;

public class ClassUtils {

    public static String path2Classname(String entryName) {
        return entryName.replace(File.separator, ".").replace(".class", "");
    }

    public static boolean checkClassName(String className) {
        return (!className.contains("R\\$") && !className.endsWith("R")
                && !className.endsWith("BuildConfig"));
    }

}