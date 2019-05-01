package com.laughfly.rxsociallib.delegate;

import android.content.Intent;

/**
 *
 */
public class DefaultDelegateActivity extends SocialDelegateActivity {
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        invokeHandleResult(requestCode, resultCode, data);
    }
}