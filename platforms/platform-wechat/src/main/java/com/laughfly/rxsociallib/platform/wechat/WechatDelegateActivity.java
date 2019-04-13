package com.laughfly.rxsociallib.platform.wechat;

import android.content.Intent;
import android.os.Bundle;

import com.laughfly.rxsociallib.Logger;
import com.laughfly.rxsociallib.delegate.ResultHandler;
import com.laughfly.rxsociallib.delegate.SocialActivity;

import java.lang.ref.WeakReference;

/**
 * 微信的结果回调类
 * author:caowy
 * date:2018-05-26
 */
public class WechatDelegateActivity extends SocialActivity {

    private String TAG = getClass().getSimpleName();

    /**
     * 设置回调监听
     * @param handler
     */
    public static void setTheResultHandler(ResultHandler<WechatDelegateActivity> handler) {
        sTempResultHandler = new WeakReference<ResultHandler<?>>(handler);
    }

    @Override
    protected void onCreateImpl(Bundle savedInstanceState) {
        Logger.w(TAG, "onCreateImpl, " + getIntent().getExtras());
        invokeHandleResult(0, 0, getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        invokeHandleResult(0, 0, intent);
        Logger.w(TAG, "onNewIntent, " + intent.getExtras());
    }

}
