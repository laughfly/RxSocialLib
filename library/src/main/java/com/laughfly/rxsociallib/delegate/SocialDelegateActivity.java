package com.laughfly.rxsociallib.delegate;

import android.app.Activity;
import android.content.Intent;

import com.laughfly.rxsociallib.SocialLogger;
import com.laughfly.rxsociallib.SocialThreads;

/**
 * 回调基类
 * author:caowy
 * date:2018-05-23
 */
public abstract class SocialDelegateActivity extends Activity {

    private String TAG = getClass().getSimpleName();

    /**
     *
     */
    protected boolean onPause;

    /**
     *
     */
    protected ResultHandler mResultHandler;

    protected boolean mHaveResult;

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

    public void setResultHandler(ResultHandler resultHandler) {
        mResultHandler = resultHandler;
        if (mResultHandler == null) {
            invokeNoResult();
        } else {
            invokeDelegateCreate(this);
        }
    }

    public void invokeHandleResult(int requestCode, int resultCode, Intent data) {
        mHaveResult = data != null;
        if(mHaveResult) {
            SocialThreads.removeUiRunnable(this);
        }
        final ResultHandler resultHandler = mResultHandler;
        if (resultHandler != null) {
            resultHandler.handleResult(requestCode, resultCode, data);
        }

        finish();
    }

    public void invokeNoResult() {
        if (mResultHandler != null) {
            mResultHandler.handleNoResult();
        }

        finish();
    }

    public void invokeDelegateCreate(SocialDelegateActivity delegate) {
        if (mResultHandler != null) {
            mResultHandler.onDelegateCreate(delegate);
        } else {
            finish();
        }
    }

    @Override
    protected void onResume() {
        SocialLogger.d(TAG, "onResume");
        super.onResume();
        if (onPause) {
            onPause = false;
            onResumeFromPause();
        }
    }

    @Override
    protected void onPause() {
        SocialLogger.d(TAG, "onPause");
        super.onPause();
        SocialThreads.removeUiRunnable(this);
        onPause = true;
    }

    @Override
    protected void onDestroy() {
        SocialLogger.d(TAG, "onDestroy");
        super.onDestroy();
        SocialThreads.removeUiRunnable(this);
        if (mResultHandler != null) {
            mResultHandler.onDelegateDestroy(this);
        }
        mResultHandler = null;
    }

    protected void onResumeFromPause() {
        SocialLogger.d(TAG, "onResumeFromPause");
        if(!mHaveResult) {
            SocialThreads.postOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!isFinishing() && !mHaveResult) {
                        invokeNoResult();
                    }
                }
            }, SocialDelegateActivity.this, 300);
        }
    }
}
