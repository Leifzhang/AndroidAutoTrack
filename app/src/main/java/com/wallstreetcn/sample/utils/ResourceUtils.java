package com.wallstreetcn.sample.utils;

import android.content.res.Resources;
import android.view.View;

public class ResourceUtils {
    public static String getResourceEntryName(View view) {
        try {
            return view.getResources().getResourceEntryName(view.getId());
        } catch (Exception e) {
            return "no_id";
        }
    }
}
