package com.laughfly.rxsociallib.platform.qq;

import android.content.Intent;

import com.laughfly.rxsociallib.delegate.SocialDelegateActivity;

public class QQDelegateActivity extends SocialDelegateActivity {

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        invokeHandleResult(requestCode, resultCode, data);
    }

}
