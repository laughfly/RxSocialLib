package com.laughfly.rxsociallib;

/**
 * 登录或分享平台
 * author:caowy
 * date:2018-04-20
 */
public enum Platform {
    QQ, QQZone, Wechat, WechatMoments, Weibo;

    static Platform lookup(String day) {
        for (Platform p : Platform.values()) {
            if (p.name().equalsIgnoreCase(day)) {
                return p;
            }
        }
        return null;
    }
}
