package com.laughfly.rxsociallib.internal;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.CallSuper;

import com.laughfly.rxsociallib.SocialCallback;
import com.laughfly.rxsociallib.SocialLogger;
import com.laughfly.rxsociallib.SocialThreads;
import com.laughfly.rxsociallib.SocialUtils;
import com.laughfly.rxsociallib.delegate.ResultHandler;
import com.laughfly.rxsociallib.delegate.SocialDelegateActivity;
import com.laughfly.rxsociallib.exception.SocialException;

import java.lang.ref.WeakReference;

/**
 * 社会化操作的基础类
 * author:caowy
 * date:2018-04-20
 *
 * @param <Builder>
 * @param <Delegate>
 * @param <Result>
 */
public abstract class SocialAction<Builder extends SocialBuilder, Delegate extends SocialDelegateActivity,
    Result extends SocialResult> implements ResultHandler<Delegate> {

    private String TAG = getClass().getSimpleName();

    /**
     *
     */
    protected Builder mBuilder;
    /**
     *
     */
    private WeakReference<Delegate> mDelegateRef;

    /**
     *
     */
    private final SocialCallback.Wrapper<Result> mCallback;

    public SocialAction() {
        mCallback = new SocialCallback.Wrapper<>();
    }

    public SocialAction setBuilder(Builder builder) {
        mBuilder = builder;
        return this;
    }

    public SocialAction setCallback(SocialCallback<Result> callback) {
        mCallback.callback = callback;
        return this;
    }

    @Override
    public void handleResult(int requestCode, int resultCode, Intent data) {
        SocialLogger.d(TAG, "handleResult: req[%d]result[%d]data[%s]", requestCode, resultCode, SocialUtils.bundle2String(data != null ? data.getExtras() : null));
    }

    @Override
    public void handleNoResult() {
        SocialLogger.d(TAG, "handleNoResult");
        finishWithNoResult();
    }

    @Override
    @CallSuper
    public void onDelegateCreate(Delegate delegate) {
        SocialLogger.d(TAG, "onDelegateCreate: %s", delegate);
        mDelegateRef = new WeakReference<>(delegate);
    }

    @Override
    @CallSuper
    public void onDelegateDestroy(Delegate delegate) {
        SocialLogger.d(TAG, "onDelegateDestroy: %s", delegate);
        mDelegateRef = null;
    }


    public void start() {
        SocialThreads.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    onStart();
                    startImpl();
                    mCallback.onStart(mBuilder.getPlatform());
                } catch (Exception e) {
                    e.printStackTrace();
                    finishWithError(e);
                }
            }
        });

    }

    protected void onStart() throws Exception {}

    protected void finishWithSuccess(Result result) {
        try {
            mCallback.onSuccess(mBuilder.getPlatform(), result);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void finish() {
        try {
            finishImpl();
            mCallback.onFinish(mBuilder.getPlatform());
        } catch (Exception e) {
            e.printStackTrace();
        }
        final Delegate delegate = getDelegate();
        if (delegate != null && !delegate.isFinishing()) {
            SocialThreads.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        delegate.finish();
                    } catch (Exception ignore) {
                    }
                }
            });
        }
    }

    /**
     * TODO
     * 保留，开发中
     */
    protected void cancel() {

    }

    protected void finishWithError(SocialException e) {
        try {
            mCallback.onError(getPlatform(), e);
            finish();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    protected abstract void finishWithCancel();

    protected abstract void finishWithNoResult();

    protected abstract void finishWithError(Exception e);

    protected String getPlatform() {
        return mBuilder.getPlatform();
    }

    protected Context getContext() {
        return mBuilder.getContext();
    }

    protected Builder getBuilder() {
        return mBuilder;
    }

    protected Delegate getDelegate() {
        return mDelegateRef != null ? mDelegateRef.get() : null;
    }


    protected abstract void startImpl();

    /**
     * 操作结束
     */
    protected abstract void finishImpl();

}
