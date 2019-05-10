package com.laughfly.rxsociallib.platform.weibo;

import android.content.Intent;

import com.laughfly.rxsociallib.AccessToken;
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
import com.sina.weibo.sdk.network.IRequestService;
import com.sina.weibo.sdk.network.impl.RequestParam;
import com.sina.weibo.sdk.network.impl.RequestService;
import com.sina.weibo.sdk.network.target.SimpleTarget;

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
        if(mBuilder.isLogoutOnly()) {
            clearAccessToken();
            finishWithLogout();
        } else {
            if(mBuilder.isClearLastAccount()) {
                clearAccessToken();
            }
            AccessToken accessToken = readAccessToken();
            if(accessToken != null) {
                updateAccessToken(accessToken);
            } else {
               login();
            }
        }
    }

    private void login() {
        try {
            mSsoHandler = new SsoHandler(getDelegate());
            mSsoHandler.authorize(this);
        } catch (Exception e) {
            e.printStackTrace();
            finishWithError(e);
        }
    }
    @Override
    public void handleResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) return;
        mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
    }

    private void updateAccessToken(AccessToken accessToken) {
        IRequestService requestService = RequestService.getInstance();
        RequestParam.Builder builder = new RequestParam.Builder(mBuilder.getContext());
        builder.setShortUrl("https://api.weibo.com/oauth2/access_token");
        builder.addPostParam("client_id", mBuilder.getAppId());
        builder.addPostParam("appKey", mBuilder.getAppId());
        builder.addPostParam("grant_type", "refresh_token");
        builder.addPostParam("refresh_token", accessToken.refreshToken);
        requestService.asyncRequest(builder.build(), new SimpleTarget() {
            @Override
            public void onSuccess(String response) {
                Oauth2AccessToken refreshToken = Oauth2AccessToken.parseAccessToken(response);
                setAccessToken(refreshToken);
            }

            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
                login();
            }
        });
    }

    private void setAccessToken(Oauth2AccessToken accessToken) {
        try {
            if (accessToken.isSessionValid()) {
                SocialLoginResult loginResult = new SocialLoginResult();
                AccessToken convertToken = AccessTokenConverter.convert(accessToken, accessToken.getUid());
                loginResult.platform = getPlatform();
                loginResult.openId = accessToken.getUid();
                loginResult.accessToken = convertToken;
                loginResult.resultObject = accessToken;

                if(mBuilder.isSaveAccessToken()) {
                    saveAccessToken(convertToken);
                }
                finishWithSuccess(loginResult);
            } else {
                if(mBuilder.isSaveAccessToken()) {
                    clearAccessToken();
                }
                int code = 0;
                try {
                    code = Integer.parseInt(accessToken.getBundle().getString("code"));
                } catch (Exception e) {
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
    public void onSuccess(Oauth2AccessToken accessToken) {
        setAccessToken(accessToken);
    }

    @Override
    public void cancel() {
        finishWithCancel();
    }

    @Override
    public void onFailure(WbConnectErrorMessage wbConnectErrorMessage) {
        try {
            finishWithError(new SocialLoginException(getPlatform(), SocialConstants.ERR_REQUEST_FAIL,
                Integer.parseInt(wbConnectErrorMessage.getErrorCode()), wbConnectErrorMessage.getErrorMessage(), wbConnectErrorMessage));
        } catch (Exception e) {
            e.printStackTrace();
            finishWithError(e);
        }
    }
}
