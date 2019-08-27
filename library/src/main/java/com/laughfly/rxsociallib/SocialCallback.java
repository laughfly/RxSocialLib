package com.laughfly.rxsociallib;

import android.support.annotation.MainThread;

import com.laughfly.rxsociallib.exception.SocialException;

import static com.laughfly.rxsociallib.SocialThreads.runOnUiThread;

/**
 * 结果回调
 * author:caowy
 * date:2018-04-20
 *
 */
public abstract class SocialCallback<P, R> {

    /**
     * 开始
     * @param platform
     */
    @MainThread
    public void onStart(String platform, P params){}

    /**
     * 发生错误
     *
     * @param platform
     * @param e
     */
    @MainThread
    public abstract void onError(String platform, P params, SocialException e);

    /**
     * 操作成功
     *
     * @param platform
     * @param resp
     */
    @MainThread
    public abstract void onSuccess(String platform, P params, R resp);

    /**
     * 操作结束，不管有没有结果或者结果是什么
     *
     * @param platform
     */
    @MainThread
    public void onFinish(String platform, P params){}

    public static class Wrapper<P, R> extends SocialCallback<P, R>{

        public SocialCallback<P, R> callback;

        @Override
        public void onStart(final String platform, final P params) {
            if(callback != null) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        callback.onStart(platform, params);
                    }
                });
            }
        }

        @Override
        public void onError(final String platform, final P params, final SocialException e) {
            if(callback != null) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        callback.onError(platform, params, e);
                    }
                });
            }
        }

        @Override
        public void onSuccess(final String platform, final P params, final R resp) {
            if(callback != null) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        callback.onSuccess(platform, params, resp);
                    }
                });
            }
        }

        @Override
        public void onFinish(final String platform, final P params) {
            if(callback != null) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        callback.onFinish(platform, params);
                    }
                });
            }
        }
    }
}
