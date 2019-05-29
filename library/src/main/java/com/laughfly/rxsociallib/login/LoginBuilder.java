package com.laughfly.rxsociallib.login;

import android.content.Context;

import com.laughfly.rxsociallib.PlatformConfig;
import com.laughfly.rxsociallib.internal.SocialBuilder;

/**
 * 登录参数Builder
 * author:caowy
 * date:2018-05-26
 */
public class LoginBuilder extends SocialBuilder<LoginParams>{

    public LoginBuilder(Context context, String platform, PlatformConfig platformConfig) {
        super(context, platform, platformConfig);
    }

    @Override
    protected LoginParams createParams() {
        return new LoginParams();
    }

    public LoginExecutor build() {
        return new LoginExecutor(mParams);
    }


    public LoginBuilder setFetchUserProfile(boolean fetchUserProfile) {
        mParams.setFetchUserProfile(fetchUserProfile);
        return this;
    }

    public LoginBuilder setSaveAccessToken(boolean saveAccessToken) {
        mParams.setSaveAccessToken(saveAccessToken);
        return this;
    }

    public LoginBuilder setClearLastAccount(boolean clearLastAccount) {
        mParams.setClearLastAccount(clearLastAccount);
        return this;
    }

    public LoginBuilder setLogoutOnly(boolean logoutOnly) {
        mParams.setLogoutOnly(logoutOnly);
        return this;
    }

    public LoginBuilder setServerSideMode(boolean serverSideMode) {
        mParams.setServerSideMode(serverSideMode);
        return this;
    }

}
