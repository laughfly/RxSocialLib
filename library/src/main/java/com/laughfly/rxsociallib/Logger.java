package com.laughfly.rxsociallib;

import android.text.TextUtils;
import android.util.Log;

public class Logger {

    private static boolean sShowLog = false;

    public static void setLogEnabled(boolean enable) {
        Logger.sShowLog = enable;
    }

    public static void i(String tag, String msg) {
        if (Logger.sShowLog) {
            if (TextUtils.isEmpty(msg)) {
                Log.i(tag, "NULL");
            } else {
                Log.i(tag, msg);
            }
        }
    }


    public static void v(String tag, String msg) {
        if (Logger.sShowLog) {
            if (TextUtils.isEmpty(msg)) {
                Log.v(tag, "msg is NULL");
            } else {
                Log.v(tag, msg);
            }
        }
    }

    public static void d(String tag, String msg) {
        if (Logger.sShowLog) {
            if (TextUtils.isEmpty(msg)) {
                Log.d(tag, "msg is NULL");
            } else {
                if(msg.length() > 1024) {
                    Log.d(tag, msg.substring(0, 1024));
                    Log.d(tag, msg.substring(1024));
                } else {
                    Log.d(tag, msg);
                }
            }
        }
    }

    public static void w(String tag, String msg, Exception ex) {
        if (Logger.sShowLog) {
            if (TextUtils.isEmpty(msg)) {
                Log.w(tag, "NULL", ex);
            } else {
                Log.w(tag, msg, ex);
            }
        }
    }

    public static void w(String tag, String msg) {
        if (Logger.sShowLog) {
            if (TextUtils.isEmpty(msg)) {
                Log.w(tag, "NULL");
            } else {
                Log.w(tag, msg);
            }
        }
    }

    public static void e(String tag, String msg) {
        if (Logger.sShowLog) {
            if (TextUtils.isEmpty(msg)) {
                Log.e(tag, "NULL");
            } else {
                Log.e(tag, msg);
            }
        }
    }

}
