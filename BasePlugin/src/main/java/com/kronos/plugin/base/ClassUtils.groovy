package com.kronos.plugin.base

class ClassUtils {

    static String path2Classname(String entryName) {
        return entryName.replace(File.separator, ".").replace(".class", "")
    }

    static boolean checkClassName(String className) {
        return (!className.contains("R\$") && !className.endsWith("R")
                && !className.endsWith("BuildConfig"))
    }

}