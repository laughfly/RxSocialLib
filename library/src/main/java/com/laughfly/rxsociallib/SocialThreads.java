package com.laughfly.rxsociallib;

import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by caowy on 2019/4/4.
 * email:cwy.fly2@gmail.com
 */
public class SocialThreads {

    private static ExecutorService sExecutorService;

    private static Handler sUiThreadHandler;

    public static synchronized void runOnThread(Runnable r) {
        if (sExecutorService == null) {
            sExecutorService = Executors.newCachedThreadPool();
        }
        sExecutorService.execute(r);
    }

    private static void ensureUiThreadHandler() {
        if (sUiThreadHandler == null) {
            synchronized (SocialUtils.class) {
                if (sUiThreadHandler == null) {
                    sUiThreadHandler = new Handler(Looper.getMainLooper());
                }
            }
        }
    }

    public static void runOnUiThread(Runnable r) {
        runOnUiThread(r, null);
    }

    public static void runOnUiThread(Runnable r, Object token) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            postOnUiThread(r, token, 0);
        } else {
            r.run();
        }
    }

    public static void postOnUiThread(Runnable r, long delayMillis) {
        postOnUiThread(r, null, delayMillis);
    }

    public static void postOnUiThread(Runnable r, Object token, long delayMillis) {
        ensureUiThreadHandler();
        sUiThreadHandler.postAtTime(r, token, SystemClock.uptimeMillis() + delayMillis);
    }

    public static void removeUiRunnable(Object token) {
        ensureUiThreadHandler();
        sUiThreadHandler.removeCallbacksAndMessages(token);
    }

    public static void removeUiRunnable(Runnable r) {
        ensureUiThreadHandler();
        sUiThreadHandler.removeCallbacks(r);
    }

    public static void removeAllUiRunnables() {
        ensureUiThreadHandler();
        sUiThreadHandler.removeCallbacksAndMessages(null);
    }
}
