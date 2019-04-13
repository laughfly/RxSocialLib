package com.laughfly.rxsociallib;

import android.support.annotation.MainThread;

import com.laughfly.rxsociallib.exception.SocialException;

/**
 * 结果回调
 * author:caowy
 * date:2018-04-20
 *
 * @param <T>
 */
public abstract class SocialCallback<T> {

    /**
     * 开始
     * @param platform
     */
    @MainThread
    public void onStart(String platform){}

    /**
     * 发生错误
     *
     * @param platform
     * @param e
     */
    @MainThread
    public abstract void onError(String platform, SocialException e);

    /**
     * 操作成功
     *
     * @param platform
     * @param resp
     */
    @MainThread
    public abstract void onSuccess(String platform, T resp);

    /**
     * 操作结束，不管有没有结果或者结果是什么
     *
     * @param platform
     */
    @MainThread
    public void onFinish(String platform){}

    public static class Wrapper<T> extends SocialCallback<T>{

        public SocialCallback<T> callback;

        @Override
        public void onStart(final String platform) {
            if(callback != null) {
                SocialThreads.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        callback.onStart(platform);
                    }
                });
            }
        }

        @Override
        public void onError(final String platform, final SocialException e) {
            if(callback != null) {
                SocialThreads.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        callback.onError(platform, e);
                    }
                });
            }
        }

        @Override
        public void onSuccess(final String platform, final T resp) {
            if(callback != null) {
                SocialThreads.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        callback.onSuccess(platform, resp);
                    }
                });
            }
        }

        @Override
        public void onFinish(final String platform) {
            if(callback != null) {
                SocialThreads.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        callback.onFinish(platform);
                    }
                });
            }
        }
    }
}
