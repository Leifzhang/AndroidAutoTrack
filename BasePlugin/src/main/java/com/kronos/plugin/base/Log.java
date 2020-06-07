package com.kronos.plugin.base;

import org.codehaus.groovy.runtime.InvokerHelper;

class Log {

    public static void info(Object msg) {
        try {
            System.out.println(InvokerHelper.
                    toString(String.format("{%s}", msg.toString())));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}