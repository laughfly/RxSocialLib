package com.laughfly.rxsociallib.login;

import com.laughfly.rxsociallib.AccessToken;
import com.laughfly.rxsociallib.internal.SocialResult;

/**
 * 登录结果
 * author:caowy
 * date:2018-05-26
 */
public class LoginResult implements SocialResult{

    public String platform;

    public String unionId;

    public String openId;

    public String serverAuthCode;

    public boolean logoutOnly;

    public Object resultObject;

    public AccessToken accessToken;

    public UserInfo userInfo;

}
