package com.kronos.plugin.multi

import java.lang.reflect.Field
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

/**
 *
 *  @Author LiABao
 *  @Since 2021/3/8
 *
 */
fun <T> Class<T>.getDeclaredFieldOrSuper(fieldName: String): Field? {
    var field: Field?
    var clazz: Class<in Any> = this as Class<Any>
    while (clazz != Any::class.java) {
        try {
            field = clazz.getDeclaredField(fieldName)
            return field
        } catch (e: java.lang.Exception) {
            // e.printStackTrace()
            //这里甚么都不要做！并且这里的异常必须这样写，不能抛出去。
            //如果这里的异常打印或者往外抛，则就不会执行clazz = clazz.getSuperclass(),最后就不会进入到父类中了
        }
        clazz = clazz.superclass
    }
    return null
}


/**
 * 循环向上转型, 获取对象的 DeclaredMethod
 *
 * @param object         : 子类对象
 * @param methodName     : 父类中的方法名
 * @param parameterTypes : 父类中的方法参数类型
 * @return 父类中的方法对象
 */
fun <T> Class<T>.getDeclaredMethodOrSuper(methodName: String, vararg parameterTypes: Class<*>?): Method? {
    var method: Method?
    var clazz: Class<*> = this
    while (clazz != Any::class.java) {
        try {
            method = clazz.getDeclaredMethod(methodName, *parameterTypes)
            return method
        } catch (e: Exception) {
            //这里甚么都不要做！并且这里的异常必须这样写，不能抛出去。
            //如果这里的异常打印或者往外抛，则就不会执行clazz = clazz.getSuperclass(),最后就不会进入到父类中了
        }
        clazz = clazz.superclass
    }
    return null
}

/**
 * 直接调用对象方法, 而忽略修饰符(private, protected, default)
 *
 * @param object         : 子类对象
 * @param methodName     : 父类中的方法名
 * @param parameterTypes : 父类中的方法参数类型
 * @param parameters     : 父类中的方法参数
 * @return 父类中方法的执行结果
 */
fun Any.invokeMethod(methodName: String, parameterTypes: Array<Class<*>?>,
                     parameters: Array<Any?>): Any? {
    //根据 对象、方法名和对应的方法参数 通过反射 调用上面的方法获取 Method 对象
    val method = this.javaClass.getDeclaredMethodOrSuper(methodName, *parameterTypes)
    //抑制Java对方法进行检查,主要是针对私有方法而言
    method?.isAccessible = true
    try {
        return method?.invoke(this, *parameters)
    } catch (e: IllegalArgumentException) {
        e.printStackTrace()
    } catch (e: IllegalAccessException) {
        e.printStackTrace()
    } catch (e: InvocationTargetException) {
        e.printStackTrace()
    }
    return null
}
