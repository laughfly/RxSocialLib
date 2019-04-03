package com.laughfly.rxsociallib.delegate;

import android.content.Intent;
import android.os.Bundle;

import com.laughfly.rxsociallib.PrintLog;

import java.lang.ref.WeakReference;

/**
 * 微信的结果回调类
 * author:caowy
 * date:2018-05-26
 */
public class WeixinDelegateActivity extends SocialActivity {

    private String TAG = getClass().getSimpleName();

    /**
     * 设置回调监听
     * author:caowy
     * date:2018-05-26
     *
     * @param handler
     */
    public static void setTheResultHandler(ResultHandler<WeixinDelegateActivity> handler) {
        sTempResultHandler = new WeakReference<ResultHandler<?>>(handler);
    }

    @Override
    protected void onCreateImpl(Bundle savedInstanceState) {
        PrintLog.w(TAG, "onCreateImpl, " + getIntent().getExtras());
        invokeHandleResult(0, 0, getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        invokeHandleResult(0, 0, intent);
        PrintLog.w(TAG, "onNewIntent, " + intent.getExtras());
    }

}
