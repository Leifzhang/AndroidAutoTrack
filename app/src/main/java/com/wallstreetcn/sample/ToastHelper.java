package com.wallstreetcn.sample;

import android.view.View;
import android.widget.Toast;

import com.wallstreetcn.sample.utils.ReflectUtil;


public class ToastHelper {
    public static void toast(View view) {
        String toastText = "插入的代码";
        Toast.makeText(view.getContext(), toastText, Toast.LENGTH_LONG).show();
    }

    public static void toast(Object text, View view) {
        toast(text, view, null);
    }

    public static void toast(Object text, View view, Object data) {
        try {
            String toastText = ReflectUtil.getObjectName(text, view);
            if (text instanceof String) {
                toastText = (String) text;
            } else {
                toastText = text.toString();
            }
            if (data != null) {
                toastText += data.toString();
            }
            Toast.makeText(view.getContext(), toastText, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
