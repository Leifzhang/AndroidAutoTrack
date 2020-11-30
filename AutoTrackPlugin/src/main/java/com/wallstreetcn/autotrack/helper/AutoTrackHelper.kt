package com.wallstreetcn.autotrack.helper

import com.kronos.plugin.base.AsmHelper
import com.kronos.plugin.base.Log
import javassist.ClassPool
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream

class AutoTrackHelper : AsmHelper {
    @Throws(IOException::class)
    override fun modifyClass(srcClass: ByteArray): ByteArray {
        val ctClass = ClassPool.getDefault().makeClass(ByteArrayInputStream(srcClass))
        if (ctClass.isFrozen) ctClass.defrost()
       // Log.info("javassist ctClass:${ctClass.name}")
        ctClass.superclass.let { it ->
            Log.info("javassist interface:${it.name}")
            if (it.name == "android/view/View\$OnClickListener") {
                ctClass?.methods?.forEach { method ->
                    Log.info("javassist methodName:${method.name}")
                    if (method.name == "onClick") {

                    }
                }
            }
        }
        return ModifyUtils.modifyClass(srcClass)
    }
}