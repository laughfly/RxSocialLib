package com.laughfly.rxsociallib.delegate;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.MainThread;

import com.laughfly.rxsociallib.PrintLog;

import java.lang.ref.WeakReference;

public class QQDelegateActivity extends SocialActivity {

    @MainThread
    public static void start(final Context context, ResultHandler<QQDelegateActivity> resultHandler) {
        QQDelegateActivity.sTempResultHandler = new WeakReference<ResultHandler<?>>(resultHandler);
        Intent intent = new Intent(context, QQDelegateActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreateImpl(Bundle savedInstanceState) {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        PrintLog.d("QQShare", "onActivityResult, requestCode=" + requestCode + "resultCode=" + resultCode + ", data=" + data);
        super.onActivityResult(requestCode, resultCode, data);
        invokeHandleResult(requestCode, resultCode, data);
    }

}
