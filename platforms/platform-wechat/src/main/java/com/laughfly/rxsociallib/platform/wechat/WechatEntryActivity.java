package com.laughfly.rxsociallib.platform.wechat;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.laughfly.rxsociallib.SocialLogger;
import com.laughfly.rxsociallib.SocialModel;
import com.laughfly.rxsociallib.delegate.ResultCallback;
import com.laughfly.rxsociallib.delegate.SocialDelegateActivity;
import com.laughfly.rxsociallib.miniprog.MiniProgramCallback;

import java.lang.ref.SoftReference;
import java.util.List;

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
    protected static SoftReference<ResultCallback> sTempResultHandler;

    public static void setTheResultHandler(ResultCallback handler) {
        sTempResultHandler = new SoftReference<>(handler);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SocialLogger.d(TAG, "onCreate: " + getIntent());
        handleMiniProgramCallback(getIntent());
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
        SocialLogger.d(TAG, "onNewIntent: " + getIntent());
        handleMiniProgramCallback(intent);
        invokeHandleResult(0, 0, intent);
    }

    private void handleMiniProgramCallback(Intent intent) {
        List<MiniProgramCallback> miniProgramCallbacks = SocialModel.getMiniProgramCallbacks();
        for (MiniProgramCallback callback : miniProgramCallbacks) {
            if(callback instanceof WechatMiniProgramCallback && ((WechatMiniProgramCallback) callback).accept(intent)) {
                ((WechatMiniProgramCallback) callback).handleIntent(intent);
            }
        }
    }
}
