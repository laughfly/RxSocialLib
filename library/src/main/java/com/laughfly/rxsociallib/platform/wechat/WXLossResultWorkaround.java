package com.laughfly.rxsociallib.platform.wechat;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import com.laughfly.rxsociallib.CompatUtils;
import com.laughfly.rxsociallib.SocialThreads;

import java.lang.ref.WeakReference;

/**
 * 对于微信未登录的情况下直接返回后没有任何结果回调的处理
 * author:caowy
 * date:2018-05-23
 */
public class WXLossResultWorkaround {

    /**
     * 分享有返回结果
     */
    private boolean mHaveResult;
    /**
     * 监听
     */
    private Callback mCallback;
    /**
     * Activity状态监听
     */
    private HackActivityCallback mActivityCallback;
    /**
     *
     */
    private WeakReference<Context> mContextRef;
    /**
     * 本应用包名
     */
    private String mPackageName;

    public WXLossResultWorkaround(Context context, Callback callback) {
        mContextRef = new WeakReference<>(context);
        mPackageName = context != null ? context.getPackageName() : "";
        mCallback = callback;
    }

    public Context getContext() {
        return mContextRef != null ? mContextRef.get() : null;
    }

    public void setHaveResult(boolean haveResult) {
        mHaveResult = haveResult;
    }

    public boolean haveResult() {
        return mHaveResult;
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public void start() {
        if (!CompatUtils.checkApi(14)) return;
        mActivityCallback = new HackActivityCallback();
        Application application = (Application) getContext().getApplicationContext();
        application.registerActivityLifecycleCallbacks(mActivityCallback);
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public void stop() {
        if (!CompatUtils.checkApi(14)) return;
        if (mActivityCallback != null) {
            Application application = (Application) getContext().getApplicationContext();
            application.unregisterActivityLifecycleCallbacks(mActivityCallback);
        }
        SocialThreads.removeUiRunnable(this);
    }

    public interface Callback {
        void onCallback();
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private class HackActivityCallback implements Application.ActivityLifecycleCallbacks {

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

        }

        @Override
        public void onActivityStarted(Activity activity) {

        }

        @Override
        public void onActivityResumed(Activity activity) {
            if (activity == getContext() || mPackageName.equals(activity.getPackageName())) {
                SocialThreads.postOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (!mHaveResult) {
                                mCallback.onCallback();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, WXLossResultWorkaround.this, 500);
            }
        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {

        }
    }
}
