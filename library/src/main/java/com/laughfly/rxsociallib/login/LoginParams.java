package com.laughfly.rxsociallib.login;

import com.laughfly.rxsociallib.internal.SocialParams;

/**
 * Created by caowy on 2019/5/29.
 * email:cwy.fly2@gmail.com
 */
public class LoginParams extends SocialParams {
    void setFetchUserProfile(boolean fetchUserProfile) {
        putBoolean("fetchUserProfile", fetchUserProfile);
    }

    public boolean isFetchUserProfile() {
        return getBoolean("fetchUserProfile");
    }

    void setSaveAccessToken(boolean saveAccessToken) {
        putBoolean("saveAccessToken", saveAccessToken);
    }

    public boolean isSaveAccessToken() {
        return getBoolean("saveAccessToken", true);
    }

    void setClearLastAccount(boolean clearLastAccount) {
        putBoolean("clearLastAccount", clearLastAccount);
    }

    public boolean isClearLastAccount() {
        return getBoolean("clearLastAccount", false);
    }

    void setLogoutOnly(boolean logoutOnly) {
        putBoolean("logoutOnly", logoutOnly);
    }

    public boolean isLogoutOnly() {
        return getBoolean("logoutOnly", false);
    }

    void setServerSideMode(boolean serverSideMode) {
        putBoolean("serverSideMode", serverSideMode);
    }

    public boolean isServerSideMode() {
        return getBoolean("serverSideMode", false);
    }
}
