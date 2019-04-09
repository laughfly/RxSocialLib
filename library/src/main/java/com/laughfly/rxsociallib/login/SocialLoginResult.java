package com.laughfly.rxsociallib.login;

import com.laughfly.rxsociallib.AccessToken;
import com.laughfly.rxsociallib.Platform;
import com.laughfly.rxsociallib.internal.SocialResult;

/**
 * 登录结果
 * author:caowy
 * date:2018-05-26
 */
public class SocialLoginResult implements SocialResult{

    public Platform platform;

    public Object resultObject;

    public String uid;

    public AccessToken accessToken;

    public UserInfo userInfo;

}
