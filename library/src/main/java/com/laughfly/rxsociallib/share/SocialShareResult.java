package com.laughfly.rxsociallib.share;

import com.laughfly.rxsociallib.Platform;
import com.laughfly.rxsociallib.internal.SocialResult;

/**
 * 分享结果
 * author:caowy
 * date:2018-05-26
 */
public class SocialShareResult implements SocialResult{

    private Platform platform;

    public SocialShareResult(Platform platform) {
        this.platform = platform;
    }

    public Platform getPlatform() {
        return platform;
    }

}
