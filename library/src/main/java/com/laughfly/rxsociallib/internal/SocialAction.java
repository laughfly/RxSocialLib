package com.laughfly.rxsociallib.internal;

import android.content.Context;

import com.laughfly.rxsociallib.SocialCallback;
import com.laughfly.rxsociallib.SocialThreads;
import com.laughfly.rxsociallib.delegate.ResultHandler;
import com.laughfly.rxsociallib.delegate.SocialActivity;
import com.laughfly.rxsociallib.exception.SocialException;

import java.lang.ref.WeakReference;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

/**
 * 社会化操作的基础类
 * author:caowy
 * date:2018-04-20
 *
 * @param <Builder>
 * @param <Delegate>
 * @param <Result>
 */
public abstract class SocialAction<Builder extends SocialBuilder, Delegate extends SocialActivity,
    Result extends SocialResult> implements ResultHandler<Delegate> {

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

    /**
     * 转成Rx数据
     *
     * @return
     */
    public Observable<Result> toObservable() {
        return Observable.create(new Observable.OnSubscribe<Result>() {
            @Override
            public void call(final Subscriber<? super Result> subscriber) {
                setCallback(new SocialCallback<Result>() {

                    @Override
                    public void onError(String platform, SocialException e) {
                        if (!subscriber.isUnsubscribed()) {
                            subscriber.onError(e);
                        }
                    }

                    @Override
                    public void onSuccess(String platform, Result resp) {
                        if (!subscriber.isUnsubscribed()) {
                            subscriber.onNext(resp);
                        }
                    }

                    @Override
                    public void onFinish(String platform) {
                        if (!subscriber.isUnsubscribed()) {
                            subscriber.onCompleted();
                        }
                    }
                }).start();
            }
        }).subscribeOn(AndroidSchedulers.mainThread()).observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public void handleNoResult() {
        finishWithNoResult();
    }

    @Override
    public void onDelegateCreate(Delegate delegate) {
        mDelegateRef = new WeakReference<>(delegate);
    }

    @Override
    public void onDelegateDestroy(Delegate delegate) {
        mDelegateRef = null;
    }


    public void start() {
        SocialThreads.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    startImpl();
                    mCallback.onStart(mBuilder.getPlatform());
                } catch (Exception e) {
                    e.printStackTrace();
                    finishWithError(e);
                }
            }
        });

    }

    protected void finishWithSuccess(Result result) {
        try {
            mCallback.onSuccess(mBuilder.getPlatform(), result);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void finish() {
        try {
            finishImpl();
            mCallback.onFinish(mBuilder.getPlatform());
        } catch (Exception e) {
            e.printStackTrace();
        }
        final Delegate delegate = getDelegate();
        if (delegate != null) {
            SocialThreads.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    delegate.finish();
                }
            });
        }
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
