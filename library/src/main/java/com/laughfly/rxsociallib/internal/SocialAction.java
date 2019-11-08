package com.laughfly.rxsociallib.internal;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.CallSuper;

import com.laughfly.rxsociallib.SocialCallback;
import com.laughfly.rxsociallib.SocialLogger;
import com.laughfly.rxsociallib.SocialThreads;
import com.laughfly.rxsociallib.SocialUtils;
import com.laughfly.rxsociallib.delegate.DefaultDelegateActivity;
import com.laughfly.rxsociallib.delegate.DelegateCallback;
import com.laughfly.rxsociallib.delegate.DelegateHelper;
import com.laughfly.rxsociallib.delegate.ResultCallback;
import com.laughfly.rxsociallib.delegate.SocialDelegateActivity;
import com.laughfly.rxsociallib.exception.SocialException;

import java.lang.ref.WeakReference;

/**
 * 社会化操作的基础类
 * author:caowy
 * date:2018-04-20
 *
 * @param <Result>
 */
public abstract class SocialAction<Params extends SocialParams, Result extends SocialResult> {

    protected String TAG = getClass().getSimpleName();

    protected Params mParams;

    /**
     *
     */
    private WeakReference<? extends SocialDelegateActivity> mDelegateRef;

    /**
     *
     */
    private final SocialCallback.Wrapper<Params, Result> mCallback;

    public SocialAction() {
        mCallback = new SocialCallback.Wrapper<>();
    }

    public SocialAction setParams(Params params) {
        mParams = params;
        return this;
    }

    public SocialAction setCallback(SocialCallback<Params, Result> callback) {
        mCallback.callback = callback;
        return this;
    }

    protected boolean useDelegate() {
        return true;
    }

    protected Class<? extends SocialDelegateActivity> getDelegateActivityClass(){
        return DefaultDelegateActivity.class;
    }

    protected void check() throws Exception{}

    protected void init() throws Exception{}

    protected abstract void execute() throws Exception;

    protected void release() throws Exception{}

    protected abstract void handleResult(int requestCode, int resultCode, Intent data) throws Exception;

    protected void startDelegateActivity() {
        SocialThreads.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                DelegateHelper.startActivity(getContext(), getDelegateActivityClass(), new DelegateCallbackWrapper(SocialAction.this), new ResultCallbackWrapper(SocialAction.this));
            }
        });
    }

    protected void handleNoResult() {
        finishWithNoResult();
    }

    @CallSuper
    protected void onDelegateCreate(SocialDelegateActivity delegate) {
        mDelegateRef = new WeakReference<>(delegate);
        SocialThreads.runOnThread(new Runnable() {
            @Override
            public void run() {
                try {
                    execute();
                } catch (Exception e) {
                    e.printStackTrace();
                    finishWithError(e);
                }
            }
        });
    }

    @CallSuper
    protected void onDelegateDestroy(SocialDelegateActivity delegate) {
        SocialLogger.d(TAG, "onDelegateDestroy: %s", delegate);
        mDelegateRef = null;
    }


    void start() {
        SocialThreads.runOnThread(new Runnable() {
            @Override
            public void run() {
                try {
                    mCallback.onStart(mParams.getPlatform(), mParams);

                    check();

                    init();

                    if(useDelegate()) {
                        startDelegateActivity();
                    } else {
                        execute();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    finishWithError(e);
                }
            }
        });
    }

    protected void finishWithSuccess(Result result) {
        try {
            mCallback.onSuccess(mParams.getPlatform(), mParams, result);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void finish() {
        try {
            mCallback.onFinish(mParams.getPlatform(), mParams);
            release();
        } catch (Exception e) {
            e.printStackTrace();
        }
        final SocialDelegateActivity delegate = getDelegate();
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
    public void cancel() {
    }

    protected void finishWithError(SocialException e) {
        try {
            mCallback.onError(getPlatform(), mParams, e);
            finish();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    protected String getPlatform() {
        return mParams.getPlatform();
    }

    protected Context getContext() {
        return mParams.getContext();
    }

    public Params getParams() {
        return mParams;
    }

    protected<T extends SocialDelegateActivity> T getDelegate() {
        return mDelegateRef != null ? (T) mDelegateRef.get() : null;
    }

    protected static class DelegateCallbackWrapper implements DelegateCallback<SocialDelegateActivity> {

        SocialAction action;

        public DelegateCallbackWrapper(SocialAction action) {
            this.action = action;
        }

        @Override
        public void onDelegateCreate(SocialDelegateActivity delegate) {
            SocialLogger.d(action.TAG, "onDelegateCreate: %s", delegate);
            try {
                action.onDelegateCreate(delegate);
            } catch (Exception e) {
                e.printStackTrace();
                action.finishWithError(e);
            }
        }

        @Override
        public void onDelegateDestroy(SocialDelegateActivity delegate) {
            SocialLogger.d(action.TAG, "onDelegateDestroy: %s", delegate);
            try {
                action.onDelegateDestroy(delegate);
            } catch (Exception e) {
                e.printStackTrace();
                action.finishWithError(e);
            }
        }
    }

    protected static class ResultCallbackWrapper implements ResultCallback {

        SocialAction action;

        public ResultCallbackWrapper(SocialAction action) {
            this.action = action;
        }

        @Override
        public void handleResult(int requestCode, int resultCode, Intent data) {
            SocialLogger.d(action.TAG, "handleResult: req[%d]result[%d]data[%s]", requestCode, resultCode, SocialUtils.bundle2String(data != null ? data.getExtras() : null));
            try {
                action.handleResult(requestCode, resultCode, data);
            } catch (Exception e) {
                e.printStackTrace();
                action.finishWithError(e);
            }
        }

        @Override
        public void handleNoResult() {
            SocialLogger.d(action.TAG, "handleNoResult");
            try {
                action.handleNoResult();
            } catch (Exception e) {
                e.printStackTrace();
                action.finishWithError(e);
            }
        }


    }

    protected abstract void finishWithCancel();

    protected abstract void finishWithNoResult();

    protected abstract void finishWithError(Exception e);

}
