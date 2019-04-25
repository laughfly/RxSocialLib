package com.laughfly.rxsociallib.platform.qq;

import com.laughfly.rxsociallib.share.ShareType;

/**
 * Created by caowy on 2019/4/15.
 * email:cwy.fly2@gmail.com
 */
class QQConstants {
    static final int THUMB_SIZE_LIMIT = 131072;//128KB
    static final int IMAGE_SIZE_LIMIT = 10485760;//10MB


    static final String QQ = "QQ";
    static final String QQZONE = "QQZone";

    static final int QQ_SHARE_SUPPORT  = ShareType.SHARE_WEB | ShareType.SHARE_TEXT |
        ShareType.SHARE_IMAGE | ShareType.SHARE_LOCAL_VIDEO | ShareType.SHARE_AUDIO | ShareType.SHARE_FILE |
        ShareType.SHARE_APP | ShareType.SHARE_MULTI_FILE;
    static final int QQZONE_SHARE_SUPPORT  = ShareType.SHARE_WEB | ShareType.SHARE_TEXT | ShareType.SHARE_MULTI_IMAGE |
        ShareType.SHARE_IMAGE | ShareType.SHARE_LOCAL_VIDEO;

    static final String QQ_PACKAGE_NAME = "com.tencent.mobileqq";

    static final String QQ_SHARE_TARGET_CLASS = "com.tencent.mobileqq.activity.JumpActivity";

    static final String TIM_PACKAGE_NAME = "com.tencent.tim";

    static final String TIM_SHARE_TARGET_CLASS = "com.tencent.mobileqq.activity.JumpActivity";
}
