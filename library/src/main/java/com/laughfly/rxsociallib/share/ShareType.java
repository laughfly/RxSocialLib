package com.laughfly.rxsociallib.share;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by caowy on 2019/4/16.
 * email:cwy.fly2@gmail.com
 */
public interface ShareType {
    int SHARE_NONE = 0;
    int SHARE_TEXT = 1;
    int SHARE_WEB = 1 << 1;
    int SHARE_IMAGE = 1 << 2;
    int SHARE_MULTI_IMAGE = 1 << 3;
    int SHARE_LOCAL_VIDEO = 1 << 4;
    int SHARE_AUDIO = 1 << 5;
    int SHARE_FILE = 1 << 6;
    int SHARE_APP = 1 << 7;
    int SHARE_MINI_PROGRAM = 1 << 8;
    int SHARE_MULTI_FILE = 1 << 9;
    int SHARE_NETWORK_VIDEO = 1 << 10;
    int SHARE_START_MINI_PROGRAM = 1 << 11;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef(flag = true, value = {SHARE_NONE, SHARE_TEXT, SHARE_WEB, SHARE_IMAGE, SHARE_MULTI_IMAGE,
        SHARE_LOCAL_VIDEO, SHARE_AUDIO, SHARE_FILE, SHARE_APP, SHARE_MINI_PROGRAM, SHARE_MULTI_FILE,
        SHARE_NETWORK_VIDEO, SHARE_START_MINI_PROGRAM})
    @interface Def {}
}
