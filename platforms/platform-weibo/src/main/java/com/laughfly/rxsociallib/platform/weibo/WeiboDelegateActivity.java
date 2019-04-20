package com.laughfly.rxsociallib.platform.weibo;

import android.content.Intent;

import com.laughfly.rxsociallib.delegate.SocialDelegateActivity;

/**
 * 微博的结果回调类
 * author:caowy
 * date:2018-05-26
 */
public class WeiboDelegateActivity extends SocialDelegateActivity {

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
