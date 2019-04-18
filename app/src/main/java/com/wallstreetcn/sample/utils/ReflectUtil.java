package com.wallstreetcn.sample.utils;

import android.view.View;

import java.lang.reflect.Field;

import androidx.recyclerview.widget.RecyclerView;

public class ReflectUtil {
    public static String getObjectName(Object object, View view) {
        StringBuilder stringBuilder = new StringBuilder();
        String target = (String) getFieldValue(object, "mTargetClassName");
        target = target.substring(target.lastIndexOf("/") + 1);
        stringBuilder.append(target);
        stringBuilder.append("_").append(ResourceUtils.getResourceEntryName(view));
        if (object instanceof RecyclerView.ViewHolder) {
            int index = ((RecyclerView.ViewHolder) object).getAdapterPosition();
            stringBuilder.append("_").append(index);
        }

        return stringBuilder.toString();
    }

    /**
     * 循环向上转型, 获取对象的 DeclaredField
     *
     * @param object    : 子类对象
     * @param fieldName : 父类中的属性名
     * @return 父类中的属性对象
     */

    public static Field getDeclaredField(Object object, String fieldName) {
        Field field = null;

        Class<?> clazz = object.getClass();

        for (; clazz != Object.class; clazz = clazz.getSuperclass()) {
            try {
                field = clazz.getDeclaredField(fieldName);
                return field;
            } catch (Exception e) {
                //这里甚么都不要做！并且这里的异常必须这样写，不能抛出去。
                //如果这里的异常打印或者往外抛，则就不会执行clazz = clazz.getSuperclass(),最后就不会进入到父类中了

            }
        }
        return null;
    }


    /**
     * 直接读取对象的属性值, 忽略 private/protected 修饰符, 也不经过 getter
     *
     * @param object    : 子类对象
     * @param fieldName : 父类中的属性名
     * @return : 父类中的属性值
     */

    public static Object getFieldValue(Object object, String fieldName) {

        //根据 对象和属性名通过反射 调用上面的方法获取 Field对象
        Field field = getDeclaredField(object, fieldName);

        //抑制Java对其的检查
        field.setAccessible(true);

        try {
            //获取 object 中 field 所代表的属性值
            return field.get(object);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
