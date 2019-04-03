package com.laughfly.rxsociallib.login;

import android.content.Intent;

import com.laughfly.rxsociallib.ErrConstants;
import com.laughfly.rxsociallib.Platform;
import com.laughfly.rxsociallib.PrintLog;
import com.laughfly.rxsociallib.SocialUtils;
import com.laughfly.rxsociallib.delegate.QQDelegateActivity;
import com.laughfly.rxsociallib.exception.SocialLoginException;
import com.laughfly.rxsociallib.internal.AccessToken;
import com.laughfly.rxsociallib.internal.AccessTokenKeeper;
import com.tencent.connect.UserInfo;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONObject;

/**
 * QQ登录
 * author:caowy
 * date:2018-05-26
 */
public class QQLogin extends AbsSocialLogin<QQDelegateActivity> implements IUiListener {

    private Tencent mTencent;

    private SocialLoginResult mResult;

    QQLogin(LoginBuilder builder) {
        super(builder);
    }

    @Override
    protected void startImpl() {
        SocialUtils.runOnUi(new Runnable() {
            @Override
            public void run() {
                try {
                    if(!SocialUtils.isQQInstalled(mBuilder.getContext())) {
                        finishWithError(new SocialLoginException(getPlatform(), ErrConstants.ERR_APP_NOT_INSTALL));
                        return;
                    }
                    QQDelegateActivity.start(mBuilder.getContext(), QQLogin.this);
                } catch (Exception e) {
                    e.printStackTrace();
                    finishWithError(e);
                }
            }
        });
    }

    @Override
    protected void finishImpl() {
    }

    @Override
    public void onComplete(Object o) {
        try {
            if(mBuilder.isFetchUserProfile()) {
                fetchUserProfile(o);
            } else {
                fetchBaseProfile(o);
            }
        } catch (Exception e) {
            e.printStackTrace();
            finishWithError(e);
        }
    }

    private void fetchUserProfile(Object o) {
        JSONObject jsonObject = (JSONObject) o;
        int ret = jsonObject.optInt("ret");
        String msg = jsonObject.optString("msg");
        if(ret == 0) {
            if (mResult == null) {
                mResult = new SocialLoginResult();
                mResult.platform = getPlatform();
            }
            String accessToken = jsonObject.optString("access_token");
            if(accessToken != null && accessToken.length() > 0) { //拿到accessToken
                //从QQ获得用户信息，进行鉴权
                String openid = jsonObject.optString("openid");
                long expiresIn = jsonObject.optLong("expires_in");
                mTencent.setAccessToken(accessToken, expiresIn + "");
                mTencent.setOpenId(openid);
                UserInfo userInfo = new UserInfo(mBuilder.getContext(), mTencent.getQQToken());
                userInfo.getUserInfo(this);
            } else { //拿到用户信息
                String nickname = jsonObject.optString("nickname");
                String gender = jsonObject.optString("gender");
                String figureurl_qq_1 = jsonObject.optString("figureurl_qq_1");
                String figureurl_qq_2 = jsonObject.optString("figureurl_qq_2");

                mResult.uid = mTencent.getOpenId();

                AccessToken _accessToken = new AccessToken();
                _accessToken.openId = mTencent.getOpenId();
                _accessToken.accessToken = mTencent.getAccessToken();
                _accessToken.expiresIn = mTencent.getExpiresIn();
                mResult.accessToken = _accessToken;

                com.laughfly.rxsociallib.login.UserInfo userInfo = new com.laughfly.rxsociallib.login.UserInfo();
                userInfo.nickname = (nickname);
                userInfo.gender = "男".equals(gender) ? 1 :  "女".equals(gender) ? 0 : 2;
                userInfo.avatarUrl = !SocialUtils.isEmpty(figureurl_qq_2) ? figureurl_qq_2 : figureurl_qq_1;
                mResult.userInfo = userInfo;

                mResult.resultObject = o;
                finishWithSuccess(mResult);
            }
        } else {
            PrintLog.e("SocialLogin", "QQ, errCode=" + ret);
            finishWithError(new SocialLoginException(getPlatform(), ErrConstants.ERR_OTHER, ret, msg, o));
        }
    }

    private void fetchBaseProfile(Object o) {
        JSONObject jsonObject = (JSONObject) o;
        int ret = jsonObject.optInt("ret");
        String msg = jsonObject.optString("msg");
        if(ret == 0) {
            SocialLoginResult result = new SocialLoginResult();
            result.platform = getPlatform();
            String accessToken = jsonObject.optString("access_token");
            if(accessToken != null && accessToken.length() > 0) { //拿到accessToken
                String openid = jsonObject.optString("openid");
                long expiresIn = jsonObject.optLong("expires_in");
                AccessToken _accessToken = new AccessToken();
                _accessToken.openId = openid;
                _accessToken.accessToken = accessToken;
                _accessToken.expiresIn = expiresIn;
                result.accessToken = _accessToken;
                result.uid = openid;
                result.resultObject = o;
                finishWithSuccess(result);
            } else {
                finishWithError(new SocialLoginException(getPlatform(), ErrConstants.ERR_OTHER, ret, msg, o));
            }
        }else {
            PrintLog.e("SocialLogin", "QQ, errCode=" + ret);
            finishWithError(new SocialLoginException(getPlatform(), ErrConstants.ERR_OTHER, ret, msg, o));
        }
    }

    @Override
    public void onError(UiError uiError) {
        finishWithError(new SocialLoginException(getPlatform(), ErrConstants.ERR_OTHER, uiError.errorCode, uiError.errorMessage, uiError));
    }

    @Override
    public void onCancel() {
        finishWithCancel();
    }

    @Override
    public void handleResult(int requestCode, int resultCode, Intent data) {
        try {
            Tencent.handleResultData(data, QQLogin.this);
        } catch (Exception e) {
            e.printStackTrace();
            finishWithError(e);
        }
    }

    @Override
    public void doOnDelegateCreate(final QQDelegateActivity activity) {
        SocialUtils.runOnBackground(new Runnable() {
            @Override
            public void run() {
                try {
                    mTencent = Tencent.createInstance(mBuilder.getAppId(), mBuilder.getContext());
                    AccessToken accessToken = AccessTokenKeeper.readAccessToken(mBuilder.getContext(), Platform.QQ);
                    if(accessToken != null) {
                        mTencent.setAccessToken(accessToken.accessToken, accessToken.expiresIn + "");
                        mTencent.setOpenId(accessToken.openId);
                        if(mTencent.isSessionValid()) {
                            mTencent.logout(mBuilder.getContext());
                        }
                    }
                    mTencent.login(activity, mBuilder.getScope(), QQLogin.this);
                } catch (Exception e) {
                    e.printStackTrace();
                    finishWithError(e);
                }
            }
        });
    }

}
