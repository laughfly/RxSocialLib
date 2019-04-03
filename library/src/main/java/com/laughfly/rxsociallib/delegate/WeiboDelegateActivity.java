package com.laughfly.rxsociallib.delegate;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.MainThread;

import java.lang.ref.WeakReference;

/**
 * 微博的结果回调类
 * author:caowy
 * date:2018-05-26
 */
public class WeiboDelegateActivity extends SocialActivity{


    @MainThread
    public static void start(final Context context, ResultHandler resultHandler) {
        WeiboDelegateActivity.sTempResultHandler = new WeakReference<ResultHandler<?>>(resultHandler);
        Intent intent = new Intent(context, WeiboDelegateActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreateImpl(Bundle savedInstanceState) {

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        invokeHandleResult(0, 0, intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        invokeHandleResult(requestCode, resultCode, data);
    }

}
