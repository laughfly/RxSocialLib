package com.laughfly.rxsociallib;

import android.util.Log;

public class SocialLogger {

    private static boolean LOG_ENABLED = false;

    private static String PREFIX = "RxSocialLib::";

    public static void setLogEnabled(boolean enable) {
        SocialLogger.LOG_ENABLED = enable;
    }

    public static void v(String tag, String message, Object... args) {
        if (SocialLogger.LOG_ENABLED) {
            println(Log.VERBOSE, tag, message, args);
        }
    }

    public static void d(String tag, String message, Object... args) {
        if (SocialLogger.LOG_ENABLED) {
            println(Log.DEBUG, tag, message, args);
        }
    }

    public static void i(String tag, String message, Object... args) {
        if (SocialLogger.LOG_ENABLED) {
            println(Log.INFO, tag, message, args);
        }
    }

    public static void w(String tag, Throwable ex) {
        if(SocialLogger.LOG_ENABLED) {
            String stackTraceString = Log.getStackTraceString(ex);
            println(Log.WARN, tag, stackTraceString);
        }
    }

    public static void w(String tag, String message, Object... args) {
        if (SocialLogger.LOG_ENABLED) {
            println(Log.WARN, tag, message, args);
        }
    }

    public static void e(String tag, String message, Object... args) {
        if (SocialLogger.LOG_ENABLED) {
            println(Log.ERROR, tag, message, args);
        }
    }

    private static void println(int priority, String tag, String message, Object... args) {
        if(args != null) {
            message = String.format(message, args);
        }
        Log.println(priority, PREFIX  + tag, message);
    }
}
