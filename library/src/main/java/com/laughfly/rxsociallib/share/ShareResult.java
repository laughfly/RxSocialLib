package com.laughfly.rxsociallib.share;

import com.laughfly.rxsociallib.internal.SocialResult;

/**
 * 分享结果
 * author:caowy
 * date:2018-05-26
 */
public class ShareResult implements SocialResult{

    private String platform;

    public ShareResult(String platform) {
        this.platform = platform;
    }

    public String getPlatform() {
        return platform;
    }

}
