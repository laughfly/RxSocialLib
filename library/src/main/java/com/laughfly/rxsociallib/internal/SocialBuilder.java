package com.laughfly.rxsociallib.internal;

import android.content.Context;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import com.laughfly.rxsociallib.PlatformConfig;
import com.laughfly.rxsociallib.SocialCallback;

import java.util.Collection;

import rx.Observable;

public abstract class SocialBuilder<Action extends SocialAction, Result> {
    private final ArrayMap<String, Object> mData = new ArrayMap<>();

    private Context mContext;

    private String mPlatform;

    public SocialBuilder(Context context, String platform, PlatformConfig platformConfig) {
        mContext = context;
        mPlatform = platform;
        if(platformConfig != null) {
            setAppId(platformConfig.appId);
            setAppSecret(platformConfig.appSecret);
            setScope(platformConfig.scope);
            setRedirectUrl(platformConfig.redirectUrl);
            setState(platformConfig.state);
        }
    }

    protected <E> void put(String key, E entity) {
        mData.put(key, entity);
    }

    protected boolean has(String key) {
        Object value = mData.get(key);
        if(value != null) {
            if(value instanceof CharSequence) {
                return !TextUtils.isEmpty((CharSequence) value);
            } else if(value instanceof Collection) {
                return !((Collection) value).isEmpty();
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    protected <E> E get(String key) {
        return (E) mData.get(key);
    }

    protected <E> E get(String key, E defaultVal) {
        Object o = mData.get(key);
        return o != null ? (E) o : defaultVal;
    }

    public Context getContext() {
        return mContext;
    }

    public String getPlatform() {
        return mPlatform;
    }

    protected SocialBuilder setAppId(String appId){
        put("appId", appId);
        return this;
    }

    public String getAppId() {
        return get("appId");
    }

    protected SocialBuilder setAppSecret(String appSecret) {
        put("appSecret", appSecret);
        return this;
    }

    public String getAppSecret() {
        return get("appSecret");
    }

    protected SocialBuilder setScope(String scope) {
        put("scope", scope);
        return this;
    }

    public String getScope() {
        return get("scope");
    }

    protected SocialBuilder setRedirectUrl(String redirectUrl) {
        put("redirectUrl", redirectUrl);
        return this;
    }

    public String getRedirectUrl( ) {
        return get("redirectUrl");
    }

    protected SocialBuilder setState(String state) {
        put("state", state);
        return this;
    }

    public String getState() {
        return get("state");
    }

    protected abstract Action build();

    public abstract Observable<Result> toObservable();

    public abstract void start(SocialCallback<Result> callback);

}
