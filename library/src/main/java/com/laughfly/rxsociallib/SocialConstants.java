package com.laughfly.rxsociallib;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * author:caowy
 * date:2018-05-04
 */
public class SocialConstants {

    public static final int ERR_OK = 0;

    public static final int ERR_API_NOT_SUPPORT = 1;

    public static final int ERR_APP_VERSION_LOW = 2;

    public static final int ERR_APP_NOT_INSTALL = 3;

    public static final int ERR_USER_CANCEL = 4;

    public static final int ERR_REQUEST_FAIL = 5;

    public static final int ERR_AUTH_DENIED = 6;

    public static final int ERR_NO_RESULT = 7;

    public static final int ERR_BAN = 8;

    public static final int ERR_CONTEXT = 9;

    public static final int ERR_SHARETYPE_NOT_SUPPORT = 10;

    public static final int ERR_DOWNLOAD_FAILED = 11;

    public static final int ERR_OTHER = 99;

    public static final int ERR_EXCEPTION = 100;

    @Retention(SOURCE)
    @IntDef(value = {ERR_OK, ERR_API_NOT_SUPPORT, ERR_APP_NOT_INSTALL, ERR_APP_VERSION_LOW, ERR_EXCEPTION, ERR_OTHER,
    ERR_USER_CANCEL, ERR_NO_RESULT, ERR_REQUEST_FAIL, ERR_AUTH_DENIED, ERR_BAN, ERR_CONTEXT, ERR_SHARETYPE_NOT_SUPPORT,
    ERR_DOWNLOAD_FAILED})
    public @interface ErrCode{}

    public static final int MINIPROGRAM_TYPE_RELEASE = 0;
    public static final int MINIPROGRAM_TYPE_TEST = 1;
    public static final int MINIPROGRAM_TYPE_PREVIEW = 2;

    @Retention(SOURCE)
    @IntDef(value = {MINIPROGRAM_TYPE_PREVIEW, MINIPROGRAM_TYPE_RELEASE, MINIPROGRAM_TYPE_TEST})
    public @interface MiniProgramType{}
}
