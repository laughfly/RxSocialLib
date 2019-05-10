package com.laughfly.rxsociallib.login;

import android.content.Context;
import android.text.TextUtils;

import com.laughfly.rxsociallib.PlatformConfig;
import com.laughfly.rxsociallib.internal.SocialBuilder;

/**
 * 登录参数Builder
 * author:caowy
 * date:2018-05-26
 */
public class LoginBuilder extends SocialBuilder<LoginAction, SocialLoginResult>{

    public LoginBuilder(Context context, String platform, PlatformConfig platformConfig) {
        super(context, platform, platformConfig);
    }

    public LoginExecutor build() {
        return new LoginExecutor(LoginBuilder.this);
    }


    public LoginBuilder setFetchUserProfile(boolean fetchUserProfile) {
        put("getUserProfile", fetchUserProfile);
        return this;
    }

    public boolean isFetchUserProfile() {
        return get("getUserProfile", false);
    }

    public LoginBuilder setSaveAccessToken(boolean saveAccessToken) {
        put("saveAccessToken", saveAccessToken);
        return this;
    }

    public boolean isSaveAccessToken() {
        return get("saveAccessToken", true);
    }

    public LoginBuilder setClearLastAccount(boolean clearLastAccount) {
        put("clearLastAccount", clearLastAccount);
        return this;
    }

    public boolean isClearLastAccount() {
        return get("clearLastAccount", false);
    }

    public LoginBuilder setLogoutOnly(boolean logoutOnly) {
        put("logoutOnly", logoutOnly);
        return this;
    }

    public boolean isLogoutOnly() {
        return get("logoutOnly", false);
    }

    public LoginBuilder setServerSideMode(boolean serverSideMode) {
        put("serverSideMode", serverSideMode);
        return this;
    }

    public boolean isServerSideMode() {
        return get("serverSideMode", false);
    }

    protected boolean checkArgs() {
        if(getContext() == null) return false;
        return !TextUtils.isEmpty(getAppId());
    }

}
