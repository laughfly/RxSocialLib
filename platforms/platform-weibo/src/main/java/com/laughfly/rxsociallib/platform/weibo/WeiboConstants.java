package com.laughfly.rxsociallib.platform.weibo;


import com.laughfly.rxsociallib.share.ShareType;

/**
 * Created by caowy on 2019/4/15.
 * email:cwy.fly2@gmail.com
 */
class WeiboConstants {

    static final int WEIBO_THUMB_LIMIT = '耀';

    static final String WEIBO = "Weibo";

    static final int WEIBO_SHARE_SUPPORT  = ShareType.SHARE_TEXT | ShareType.SHARE_WEB | ShareType.SHARE_MULTI_IMAGE |
        ShareType.SHARE_IMAGE | ShareType.SHARE_LOCAL_VIDEO;

    static final String WEIBO_STORY = "WeiboStory";

    static final int WEIBO_STORY_SHARE_SUPPORT  = ShareType.SHARE_IMAGE | ShareType.SHARE_LOCAL_VIDEO;

    static final String WEIBO_PACKAGE = "com.sina.weibo";

    static final String WEIBO_SHARE_TARGET_CLASS = "com.sina.weibo.composerinde.ComposerDispatchActivity";
}
