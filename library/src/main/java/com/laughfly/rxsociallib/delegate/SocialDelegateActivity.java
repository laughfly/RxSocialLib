package com.laughfly.rxsociallib.delegate;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

import com.laughfly.rxsociallib.SocialLogger;
import com.laughfly.rxsociallib.SocialThreads;

/**
 * 回调基类
 * author:caowy
 * date:2018-05-23
 */
public abstract class SocialDelegateActivity extends FragmentActivity {

    private String TAG = getClass().getSimpleName();

    protected ResultCallback mResultCallback;

    protected DelegateCallback mDelegateCallback;

    protected boolean mPaused;

    protected boolean mHasResult;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        SocialLogger.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        invokeOnDelegateCreate();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        SocialLogger.d(TAG, "onResume");
        super.onResume();
        if (mPaused) {
            mPaused = false;
            onResumeFromPause();
        }
    }

    @Override
    protected void onPause() {
        SocialLogger.d(TAG, "onPause");
        super.onPause();
        SocialThreads.removeUiRunnable(this);
        mPaused = true;
    }

    protected void onResumeFromPause() {
        SocialLogger.d(TAG, "onResumeFromPause");
        if(!mHasResult) {
            SocialThreads.postOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!isFinishing() && !mHasResult) {
                        invokeNoResult();
                    }
                }
            }, SocialDelegateActivity.this, 300);
        }
    }


    @Override
    protected void onDestroy() {
        SocialLogger.d(TAG, "onDestroy");
        super.onDestroy();
        SocialThreads.removeUiRunnable(this);
        invokeOnDelegateDestroy();
        mDelegateCallback = null;
        mResultCallback = null;
    }


    @Override
    protected void onNewIntent(Intent intent) {
        SocialLogger.d(TAG, "onNewIntent: %s", intent != null ? intent.toString() : "");
        super.onNewIntent(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        SocialLogger.d(TAG, "onActivityResult, requestCode=" + requestCode + "resultCode=" + resultCode + ", data=" + data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void setDelegateCallback(DelegateCallback delegateCallback) {
        mDelegateCallback = delegateCallback;
    }

    public void setResultCallback(ResultCallback resultCallback) {
        mResultCallback = resultCallback;
    }

    protected void invokeHandleResult(int requestCode, int resultCode, Intent data) {
        mHasResult = true;
        SocialThreads.removeUiRunnable(this);
        final ResultCallback resultCallback = mResultCallback;
        if (resultCallback != null) {
            resultCallback.handleResult(requestCode, resultCode, data);
        }

        finish();
    }

    public void invokeNoResult() {
        final ResultCallback resultCallback = mResultCallback;
        if (resultCallback != null) {
            resultCallback.handleNoResult();
        }
        finish();
    }

    public void invokeOnDelegateCreate() {
        final DelegateCallback delegateCallback = mDelegateCallback;
        if (delegateCallback != null) {
            delegateCallback.onDelegateCreate(this);
        }
    }

    public void invokeOnDelegateDestroy() {
        final DelegateCallback delegateCallback = mDelegateCallback;
        if (delegateCallback != null) {
            delegateCallback.onDelegateDestroy(this);
        }
    }


}
