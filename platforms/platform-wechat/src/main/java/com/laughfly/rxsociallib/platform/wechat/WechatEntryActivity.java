package com.laughfly.rxsociallib.platform.wechat;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.laughfly.rxsociallib.SocialLogger;
import com.laughfly.rxsociallib.delegate.ResultHandler;
import com.laughfly.rxsociallib.delegate.SocialDelegateActivity;

import java.lang.ref.WeakReference;

/**
 * 微信的结果回调类
 * author:caowy
 * date:2018-05-26
 */
public class WechatEntryActivity extends SocialDelegateActivity {

    private String TAG = getClass().getSimpleName();

    /**
     * 结果回调监听
     */
    protected static WeakReference<ResultHandler<?>> sTempResultHandler;

    public static void setTheResultHandler(ResultHandler<SocialDelegateActivity> handler) {
        sTempResultHandler = new WeakReference<ResultHandler<?>>(handler);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            ResultHandler<?> tempResultHandler = sTempResultHandler != null ? sTempResultHandler.get() : null;
            //可能是App重启
            if (tempResultHandler == null) {
                invokeNoResult();
            } else {
                mResultHandler = tempResultHandler;
                onCreateImpl(savedInstanceState);
            }
        } finally {
            sTempResultHandler = null;
            finish();
        }
    }

    protected void onCreateImpl(Bundle savedInstanceState) {
        SocialLogger.w(TAG, "onCreateImpl, " + getIntent().getExtras());
        invokeHandleResult(0, 0, getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        invokeHandleResult(0, 0, intent);
        SocialLogger.w(TAG, "onNewIntent, " + intent.getExtras());
    }

}
