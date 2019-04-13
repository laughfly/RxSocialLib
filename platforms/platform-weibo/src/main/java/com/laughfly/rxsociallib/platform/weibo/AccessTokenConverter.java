package com.laughfly.rxsociallib.platform.weibo;

import com.laughfly.rxsociallib.AccessToken;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;

/**
 * Created by caowy on 2019/4/12.
 * email:cwy.fly2@gmail.com
 */
class AccessTokenConverter {
    public static AccessToken convert(Oauth2AccessToken token, String openId) {
        AccessToken accessToken = new AccessToken();
        accessToken.accessToken = token.getToken();
        accessToken.expiresIn = token.getExpiresTime();
        accessToken.refreshToken = token.getRefreshToken();
        accessToken.uid = token.getUid();
        accessToken.openId = openId;
        return accessToken;
    }
}
