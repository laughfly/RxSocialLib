package com.laughfly.rxsociallib.internal;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import java.util.ArrayList;

/**
 * Created by caowy on 2019/5/23.
 * email:cwy.fly2@gmail.com
 */
public class SocialParams {
    private Context mApplicationContext;

    private Bundle mBundle = new Bundle();

    void setContext(Context applicationContext) {
        mApplicationContext = applicationContext;
    }

    public Context getContext() {
        return mApplicationContext;
    }

    protected void putString(String key, String value) {
        mBundle.putString(key, value);
    }

    protected String getString(String key) {
        return mBundle.getString(key);
    }

    protected String getString(@Nullable String key, String defaultValue) {
        return mBundle.getString(key, defaultValue);
    }

    protected void putStringArray(@Nullable String key, @Nullable String[] value) {
        mBundle.putStringArray(key, value);
    }

    protected void putStringArrayList(@Nullable String key, @Nullable ArrayList<String> value) {
        mBundle.putStringArrayList(key, value);
    }

    protected ArrayList<String> getStringArrayList(@Nullable String key) {
        return mBundle.getStringArrayList(key);
    }

    protected boolean getBoolean(String key) {
        return mBundle.getBoolean(key);
    }

    protected void putBoolean(@Nullable String key, boolean value) {
        mBundle.putBoolean(key, value);
    }

    protected boolean getBoolean(String key, boolean defaultValue) {
        return mBundle.getBoolean(key, defaultValue);
    }

    protected void putInt(String key, int value) {
        mBundle.putInt(key, value);
    }

    protected int getInt(String key) {
        return mBundle.getInt(key);
    }

    protected int getInt(String key, int defaultValue) {
        return mBundle.getInt(key, defaultValue);
    }

    protected void putParcelable(@Nullable String key, @Nullable Parcelable value) {
        mBundle.putParcelable(key, value);
    }

    protected <T extends Parcelable> T getParcelable(@Nullable String key) {
        return mBundle.getParcelable(key);
    }

    protected void putAll(Bundle bundle) {
        mBundle.putAll(bundle);
    }

    protected Object get(String key) {
        return mBundle.get(key);
    }

    protected boolean containsKey(String key) {
        return mBundle.containsKey(key);
    }

    void setPlatform(String platform) {
        putString("platform", platform);
    }

    public String getPlatform() {
        return getString("platform");
    }

    void setAppId(String appId) {
        putString("appId", appId);
    }

    public String getAppId() {
        return getString("appId");
    }

    void setAppSecret(String appSecret) {
        putString("appSecret", appSecret);
    }

    public String getAppSecret() {
        return getString("appSecret");
    }

    void setScope(String scope) {
        putString("scope", scope);
    }

    public String getScope() {
        return getString("scope");
    }

    void setRedirectUrl(String redirectUrl) {
        putString("redirectUrl", redirectUrl);
    }

    public String getRedirectUrl( ) {
        return getString("redirectUrl");
    }

    void setState(String state) {
        putString("state", state);
    }

    public String getState() {
        return getString("state");
    }
}
