package com.laughfly.rxsociallib.platform.qq;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import com.laughfly.rxsociallib.AccessToken;
import com.laughfly.rxsociallib.SocialConstants;
import com.laughfly.rxsociallib.exception.SocialLoginException;
import com.laughfly.rxsociallib.login.LoginAction;
import com.laughfly.rxsociallib.login.LoginFeature;
import com.laughfly.rxsociallib.login.LoginFeatures;
import com.laughfly.rxsociallib.login.SocialLoginResult;
import com.tencent.connect.UserInfo;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

import static com.laughfly.rxsociallib.login.UserInfo.GENDER_FEMALE;
import static com.laughfly.rxsociallib.login.UserInfo.GENDER_MALE;
import static com.laughfly.rxsociallib.login.UserInfo.GENDER_UNKNOWN;

/**
 * QQ登录
 * author:caowy
 * date:2018-05-26
 */
@LoginFeatures({
    @LoginFeature(platform = "QQ")
})
public class QQLogin extends LoginAction implements IUiListener {

    private Tencent mTencent;

    private com.laughfly.rxsociallib.login.UserInfo mUserInfo;

    private AccessToken mAccessToken;

    @Override
    protected void check() throws Exception {
//        if (!QQUtils.isQQInstalled(mBuilder.getContext()) && !QQUtils.isTimInstalled(mBuilder.getContext())) {
//            throw new SocialLoginException(getPlatform(), SocialConstants.ERR_APP_NOT_INSTALL);
//        }
    }

    @Override
    protected void init() throws Exception {
        mTencent = Tencent.createInstance(mBuilder.getAppId(), mBuilder.getContext());
    }

    @Override
    protected void execute() throws Exception {
        Activity delegate = getDelegate();
        if (mBuilder.isLogoutOnly()) {
            logoutOnly();
        } else {
            login(delegate);
        }
    }

    private void logoutOnly() {
        logout();
        finishWithLogout();
    }

    private void logout() {
        mTencent.logout(mBuilder.getContext());
    }

    private void login(Activity delegate) {
        if(mBuilder.isClearLastAccount()) {
            mTencent.logout(mBuilder.getContext());
            clearAccessToken();
        } else {
            AccessToken accessToken = readAccessToken();
            if(accessToken != null) {
                mTencent.setOpenId(accessToken.openId);
                mTencent.setAccessToken(accessToken.accessToken, accessToken.expiresIn + "");
            }
        }
        if(mTencent.isSessionValid()) {
            mTencent.logout(mBuilder.getContext());
        }
        mTencent.login(delegate, mBuilder.getScope(), this);
    }

    private void updateOrFinish() {
        if(mBuilder.isFetchUserProfile()) {
            updateUserInfo();
        } else {
            finishWithLogin();
        }
    }

    private void updateUserInfo() {
        UserInfo userInfo = new UserInfo(mBuilder.getContext(), mTencent.getQQToken());
        userInfo.getUserInfo(this);
    }

    private void setAccessToken(JSONObject jsonObject) throws JSONException {
        String access_token = jsonObject.getString("access_token");
        String expires_in = jsonObject.optString("expires_in");
        String openid = jsonObject.optString("openid");
        mTencent.setAccessToken(access_token, expires_in);
        mTencent.setOpenId(openid);

        AccessToken _accessToken = new AccessToken();
        _accessToken.openId = mTencent.getOpenId();
        _accessToken.accessToken = mTencent.getAccessToken();
        _accessToken.expiresIn = mTencent.getExpiresIn();

        mAccessToken = _accessToken;
        if(mBuilder.isSaveAccessToken()) {
            saveAccessToken(_accessToken);
        }
    }

    private void setUserInfo(JSONObject jsonObject) {
        String nickname = jsonObject.optString("nickname");
        String gender = jsonObject.optString("gender");
        String figureurl_qq_1 = jsonObject.optString("figureurl_qq_1");
        String figureurl_qq_2 = jsonObject.optString("figureurl_qq_2");

        com.laughfly.rxsociallib.login.UserInfo userInfo = new com.laughfly.rxsociallib.login.UserInfo();
        userInfo.nickname = (nickname);
        userInfo.gender = "男".equals(gender) ? GENDER_MALE : "女".equals(gender) ? GENDER_FEMALE : GENDER_UNKNOWN;
        userInfo.avatarUrl = !TextUtils.isEmpty(figureurl_qq_2) ? figureurl_qq_2 : figureurl_qq_1;

        mUserInfo = userInfo;
    }

    private void finishWithLogin() {
        SocialLoginResult result = new SocialLoginResult();
        result.platform = getPlatform();
        result.openId = mTencent.getOpenId();

        result.accessToken = mAccessToken != null ? mAccessToken : readAccessToken();

        if(mBuilder.isFetchUserProfile()) {
            result.userInfo = mUserInfo;
        }

        finishWithSuccess(result);
    }

    @Override
    public void handleResult(int requestCode, int resultCode, Intent data) throws Exception {
        Tencent.handleResultData(data, QQLogin.this);
    }

    @Override
    public void onComplete(Object o) {
        try {
            JSONObject jsonObject = (JSONObject) o;
            int ret = jsonObject.getInt("ret");
            if(0 != ret) {
                String msg = jsonObject.optString("msg");
                finishWithError(new SocialLoginException(getPlatform(), SocialConstants.ERR_REQUEST_FAIL, ret, msg, o));
                return;
            }
            if(jsonObject.has("access_token")) {
                setAccessToken(jsonObject);
                updateOrFinish();
            } else if(jsonObject.has("nickname")) {
                setUserInfo(jsonObject);
                finishWithLogin();
            }
        } catch (Exception e) {
            e.printStackTrace();
            finishWithError(e);
        }
    }

    @Override
    public void onError(UiError uiError) {
        finishWithError(new SocialLoginException(getPlatform(), SocialConstants.ERR_OTHER, uiError.errorCode, uiError.errorMessage, uiError));
    }

    @Override
    public void onCancel() {
        finishWithCancel();
    }


}
