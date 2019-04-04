package com.laughfly.rxsociallib.platform.weibo;

import android.content.Intent;

import com.laughfly.rxsociallib.ErrConstants;
import com.laughfly.rxsociallib.exception.SocialLoginException;
import com.laughfly.rxsociallib.internal.AccessToken;
import com.laughfly.rxsociallib.login.AbsSocialLogin;
import com.laughfly.rxsociallib.login.LoginBuilder;
import com.laughfly.rxsociallib.login.SocialLoginResult;
import com.sina.weibo.sdk.WbSdk;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WbAuthListener;
import com.sina.weibo.sdk.auth.WbConnectErrorMessage;
import com.sina.weibo.sdk.auth.sso.SsoHandler;

/**
 * 微博登录
 * author:caowy
 * date:2018-05-26
 */
public class WeiboLogin extends AbsSocialLogin<WeiboDelegateActivity> implements WbAuthListener {

    private SsoHandler mSsoHandler;

    public WeiboLogin(LoginBuilder builder) {
        super(builder);
    }

    @Override
    protected void startImpl() {
        WbSdk.install(mBuilder.getContext(),
            new AuthInfo(mBuilder.getContext(), mBuilder.getAppId(), mBuilder.getRedirectUrl(), mBuilder.getScope()));
        WeiboDelegateActivity.start(mBuilder.getContext(), WeiboLogin.this);
    }

    @Override
    protected void finishImpl() {
    }

    @Override
    public void handleResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) return;
        try {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        } catch (Exception e) {
            e.printStackTrace();
            finishWithError(e);
        }
    }

    @Override
    public void onDelegateCreate(WeiboDelegateActivity activity) {
        super.onDelegateCreate(activity);
        try {
            mSsoHandler = new SsoHandler(activity);
            mSsoHandler.authorize(this);
        } catch (Exception e) {
            e.printStackTrace();
            finishWithError(e);
        }
    }

    @Override
    public void onSuccess(Oauth2AccessToken accessToken) {
        try {
            if (accessToken.isSessionValid()) {
                SocialLoginResult loginResult = new SocialLoginResult();
                loginResult.platform = getPlatform();
                loginResult.uid = accessToken.getUid();
                loginResult.accessToken = AccessToken.convert(accessToken, null);
                loginResult.resultObject = accessToken;
                finishWithSuccess(loginResult);
            } else {
                int code = 0;
                try {
                    code = Integer.parseInt(accessToken.getBundle().getString("code"));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                finishWithError(new SocialLoginException(getPlatform(), ErrConstants.ERR_OTHER, code, "", accessToken));
            }
        } catch (Exception e) {
            e.printStackTrace();
            finishWithError(e);
        }
    }

    @Override
    public void cancel() {
        finishWithCancel();
    }

    @Override
    public void onFailure(WbConnectErrorMessage wbConnectErrorMessage) {
        finishWithError(new SocialLoginException(getPlatform(), ErrConstants.ERR_REQUEST_FAIL, Integer.parseInt(wbConnectErrorMessage.getErrorCode()), wbConnectErrorMessage.getErrorMessage(), wbConnectErrorMessage));
    }
}
