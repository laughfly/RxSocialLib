package com.laughfly.rxsociallib.platform.weibo;

import android.content.Intent;

import com.laughfly.rxsociallib.SocialConstants;
import com.laughfly.rxsociallib.exception.SocialLoginException;
import com.laughfly.rxsociallib.login.LoginAction;
import com.laughfly.rxsociallib.login.LoginFeature;
import com.laughfly.rxsociallib.login.LoginFeatures;
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
@LoginFeatures({
    @LoginFeature(platform = "Weibo")
})
public class WeiboLogin extends LoginAction implements WbAuthListener {

    private SsoHandler mSsoHandler;

    @Override
    protected void init() throws Exception {
        WbSdk.install(mBuilder.getContext(),
            new AuthInfo(mBuilder.getContext(), mBuilder.getAppId(), mBuilder.getRedirectUrl(), mBuilder.getScope()));
    }

    @Override
    protected void execute() throws Exception {
        mSsoHandler = new SsoHandler(getDelegate());
        mSsoHandler.authorize(this);
    }

    @Override
    public void handleResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) return;
        mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
    }

    @Override
    public void onSuccess(Oauth2AccessToken accessToken) {
        try {
            if (accessToken.isSessionValid()) {
                SocialLoginResult loginResult = new SocialLoginResult();
                loginResult.platform = getPlatform();
                loginResult.uid = accessToken.getUid();
                loginResult.accessToken = AccessTokenConverter.convert(accessToken, null);
                loginResult.resultObject = accessToken;
                finishWithSuccess(loginResult);
            } else {
                int code = 0;
                try {
                    code = Integer.parseInt(accessToken.getBundle().getString("code"));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                finishWithError(new SocialLoginException(getPlatform(), SocialConstants.ERR_OTHER, code, "", accessToken));
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
        finishWithError(new SocialLoginException(getPlatform(), SocialConstants.ERR_REQUEST_FAIL, Integer.parseInt(wbConnectErrorMessage.getErrorCode()), wbConnectErrorMessage.getErrorMessage(), wbConnectErrorMessage));
    }
}
