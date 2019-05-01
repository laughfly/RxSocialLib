package com.laughfly.rxsociallib.platform.wechat;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.laughfly.rxsociallib.delegate.ResultCallback;
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
    protected static WeakReference<ResultCallback> sTempResultHandler;

    public static void setTheResultHandler(ResultCallback handler) {
        sTempResultHandler = new WeakReference<>(handler);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            ResultCallback tempResultCallback = sTempResultHandler != null ? sTempResultHandler.get() : null;
            //可能是App重启
            if (tempResultCallback == null) {
                invokeNoResult();
            } else {
                mResultCallback = tempResultCallback;
                invokeHandleResult(0, 0, getIntent());
            }
        } finally {
            sTempResultHandler = null;
            finish();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        invokeHandleResult(0, 0, intent);
    }

}
