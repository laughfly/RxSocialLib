package com.laughfly.rxsociallib.delegate;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import java.lang.ref.WeakReference;

/**
 * 回调基类
 * author:caowy
 * date:2018-05-23
 */
public abstract class SocialActivity extends Activity {

    /**
     * 结果回调监听
     */
    protected static WeakReference<ResultHandler<?>> sTempResultHandler;

    /**
     *
     */
    private boolean onPause;

    /**
     *
     */
    private ResultHandler mResultHandler;

    /**
     *
     */
    private Handler mUiHandler;

    private boolean mHaveResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            mUiHandler = new Handler();
            ResultHandler<?> tempResultHandler = sTempResultHandler != null ? sTempResultHandler.get() : null;
            //可能是App重启
            if (tempResultHandler == null && mResultHandler == null) {
                invokeNoResult();
                return;
            } else {
                if(tempResultHandler != null) {
                    setResultHandler(tempResultHandler);
                }
                invokeDelegateCreate(this);
                onCreateImpl(savedInstanceState);
            }
        } finally {
            sTempResultHandler = null;
        }
    }

    protected abstract void onCreateImpl(Bundle savedInstanceState);

    public void setResultHandler(ResultHandler resultHandler) {
        mResultHandler = resultHandler;
    }

    public void invokeHandleResult(int requestCode, int resultCode, Intent data) {
        mHaveResult = data != null;
        if(mHaveResult) {
            mUiHandler.removeCallbacksAndMessages(null);
        }
        final ResultHandler resultHandler = mResultHandler;
        if (resultHandler != null) {
            resultHandler.handleResult(requestCode, resultCode, data);
        } else {
            finish();
        }
    }

    public void invokeNoResult() {
        if (mResultHandler != null) {
            mResultHandler.handleNoResult();
        } else {
            finish();
        }
    }

    public void invokeDelegateCreate(SocialActivity delegate) {
        if (mResultHandler != null) {
            mResultHandler.onDelegateCreate(delegate);
        } else {
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (onPause) {
            onPause = false;
            onResumeFromPause();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        onPause = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUiHandler.removeCallbacksAndMessages(null);
        if (mResultHandler != null) {
            mResultHandler.onDelegateDestroy(this);
        }
        mResultHandler = null;
    }

    protected void onResumeFromPause() {
        if(!mHaveResult) {
            mUiHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!isFinishing() && !mHaveResult) {
                        invokeNoResult();
                    }
                }
            }, 300);
        }
    }
}
