package com.laughfly.rxsociallib.platform.wechat;

import android.content.Intent;

import com.laughfly.rxsociallib.SocialThreads;
import com.laughfly.rxsociallib.miniprog.MiniProgramCallback;

public abstract class WechatMiniProgramCallback implements MiniProgramCallback {
    final boolean accept(Intent intent) {
        if (intent != null) {
            int type = intent.getIntExtra("_wxapi_command_type", 0);
            return type == 4;
        }
        return false;
    }

    final void handleIntent(Intent intent) {
        final String extMsg = intent.getStringExtra("_wxappextendobject_extInfo");
        SocialThreads.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                onCallback(extMsg);
            }
        });
    }

}
