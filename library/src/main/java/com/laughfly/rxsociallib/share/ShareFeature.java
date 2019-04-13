package com.laughfly.rxsociallib.share;

import android.support.annotation.IntDef;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by caowy on 2019/4/11.
 * email:cwy.fly2@gmail.com
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ShareFeature {
    String platform();
    @Def int supportFeatures();

    int SHARE_TEXT = 1;
    int SHARE_URL = 1 << 1;
    int SHARE_IMAGE = 1 << 2;
    int SHARE_VIDEO = 1 << 3;
    int SHARE_AUDIO = 1 << 4;
    int SHARE_FILE = 1 << 5;
    int SHARE_APP = 1 << 6;
    int SHARE_MINI_PROGRAM = 1 << 7;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef(flag = true, value = {SHARE_TEXT, SHARE_URL, SHARE_IMAGE, SHARE_VIDEO, SHARE_AUDIO, SHARE_FILE, SHARE_APP, SHARE_MINI_PROGRAM})
    @interface Def {}
}
