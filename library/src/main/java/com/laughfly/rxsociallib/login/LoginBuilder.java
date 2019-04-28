package com.laughfly.rxsociallib.login;

import android.content.Context;
import android.text.TextUtils;

import com.laughfly.rxsociallib.PlatformConfig;
import com.laughfly.rxsociallib.SocialActionFactory;
import com.laughfly.rxsociallib.internal.SocialBuilder;

/**
 * 登录参数Builder
 * author:caowy
 * date:2018-05-26
 */
public class LoginBuilder extends SocialBuilder<AbsSocialLogin, SocialLoginResult>{

    public LoginBuilder(Context context, String platform, PlatformConfig platformConfig) {
        super(context, platform, platformConfig);
    }

    @Override
    protected AbsSocialLogin build() {
        return SocialActionFactory.createLoginAction(getPlatform(), this);
    }

    public LoginBuilder setFetchUserProfile(boolean fetchUserProfile) {
        put("getUserProfile", Boolean.toString(fetchUserProfile));
        return this;
    }

    public boolean isFetchUserProfile() {
        return Boolean.parseBoolean((String)get("getUserProfile"));
    }

    protected boolean checkArgs() {
        if(getContext() == null) return false;
        return !TextUtils.isEmpty(getAppId());
    }

}
