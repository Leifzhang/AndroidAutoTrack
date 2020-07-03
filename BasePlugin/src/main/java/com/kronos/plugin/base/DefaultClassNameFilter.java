package com.kronos.plugin.base;

public class DefaultClassNameFilter implements ClassNameFilter {
    @Override
    public boolean filter(String className) {
        return false;
    }
}
