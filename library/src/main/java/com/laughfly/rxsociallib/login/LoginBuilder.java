package com.laughfly.rxsociallib.login;

import android.content.Context;

import com.laughfly.rxsociallib.PlatformConfig;
import com.laughfly.rxsociallib.SocialActionFactory;
import com.laughfly.rxsociallib.SocialCallback;
import com.laughfly.rxsociallib.SocialUtils;
import com.laughfly.rxsociallib.internal.SocialBuilder;

import rx.Observable;

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
    public Observable<SocialLoginResult> toObservable() {
        return build().toObservable();
    }

    @Override
    public void start(SocialCallback<SocialLoginResult> callback) {
        AbsSocialLogin share = build();
        share.setCallback(callback);
        share.start();
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
        return !SocialUtils.isEmpty(getAppId());
    }

}
