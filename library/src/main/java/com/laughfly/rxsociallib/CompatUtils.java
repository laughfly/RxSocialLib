package com.laughfly.rxsociallib;

import android.os.Build;

/**
 * 系统版本兼容工具
 * author:caowy
 * date:2018-05-01
 */
public class CompatUtils {

    public static boolean checkApi(int api) {
        return Build.VERSION.SDK_INT >= api;
    }

    public static boolean hasClass(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
