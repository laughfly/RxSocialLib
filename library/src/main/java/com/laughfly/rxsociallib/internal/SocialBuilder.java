package com.laughfly.rxsociallib.internal;

import android.content.Context;

import com.laughfly.rxsociallib.PlatformConfig;

public abstract class SocialBuilder<Params extends SocialParams> {

    protected Params mParams;

    public SocialBuilder(Context context, String platform, PlatformConfig platformConfig) {
        mParams = createParams();
        mParams.setContext(context);
        mParams.setPlatform(platform);
        if(platformConfig != null) {
            mParams.setAppId(platformConfig.appId);
            mParams.setAppSecret(platformConfig.appSecret);
            mParams.setScope(platformConfig.scope);
            mParams.setRedirectUrl(platformConfig.redirectUrl);
            mParams.setState(platformConfig.state);
        }
    }

    protected abstract Params createParams();

    public Params getParams() {
        return mParams;
    }
}
