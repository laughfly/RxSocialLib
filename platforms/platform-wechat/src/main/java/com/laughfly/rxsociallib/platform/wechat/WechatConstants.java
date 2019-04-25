package com.laughfly.rxsociallib.platform.wechat;

import com.laughfly.rxsociallib.share.ShareType;

/**
 * Created by caowy on 2019/4/15.
 * email:cwy.fly2@gmail.com
 */
class WechatConstants {
    static final int IMAGE_SIZE_LIMIT = 10485760;//10MB
    static final int MINI_PROG_IMAGE_LIMIT = 131072;//128KB
    static final int THUMB_SIZE_LIMIT = '耀';//32768

    static final String WECHAT = "Wechat";
    static final String WECHAT_MOMENTS = "WechatMoments";

    static final int WECHAT_SHARE_SUPPORT  = ShareType.SHARE_TEXT | ShareType.SHARE_WEB |
        ShareType.SHARE_IMAGE | ShareType.SHARE_LOCAL_VIDEO | ShareType.SHARE_AUDIO | ShareType.SHARE_FILE|
        ShareType.SHARE_MINI_PROGRAM;
    static final int WECHAT_MOMENTS_SHARE_SUPPORT  = ShareType.SHARE_TEXT | ShareType.SHARE_WEB |
        ShareType.SHARE_IMAGE | ShareType.SHARE_NETWORK_VIDEO | ShareType.SHARE_AUDIO;

    static final String WECHAT_PACKAGE = "com.tencent.mm";

    static final String WECHAT_SHARE_TARGET_CLASS = "com.tencent.mm.ui.tools.ShareImgUI";
}
