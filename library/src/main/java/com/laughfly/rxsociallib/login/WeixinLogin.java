package com.laughfly.rxsociallib.login;

import android.content.Intent;

import com.laughfly.rxsociallib.ErrConstants;
import com.laughfly.rxsociallib.PrintLog;
import com.laughfly.rxsociallib.SocialUtils;
import com.laughfly.rxsociallib.delegate.WeixinDelegateActivity;
import com.laughfly.rxsociallib.exception.SocialException;
import com.laughfly.rxsociallib.exception.SocialLoginException;
import com.laughfly.rxsociallib.internal.AccessToken;
import com.laughfly.rxsociallib.internal.WXLossResultWorkaround;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.json.JSONObject;

/**
 * 微信登录
 * author:caowy
 * date:2018-05-26
 */
public class WeixinLogin extends AbsSocialLogin<WeixinDelegateActivity> implements IWXAPIEventHandler,
    WXLossResultWorkaround.Callback {

    private IWXAPI mWXAPI;

    private WXLossResultWorkaround mWXLossResultWorkaround;

    WeixinLogin(LoginBuilder builder) {
        super(builder);
    }

    @Override
    protected void startImpl() {
        mWXLossResultWorkaround = new WXLossResultWorkaround(getContext(), this);
        mWXLossResultWorkaround.start();
        WeixinDelegateActivity.setTheResultHandler(WeixinLogin.this);
        mWXAPI = WXAPIFactory.createWXAPI(mBuilder.getContext(), mBuilder.getAppId(), true);
        mWXAPI.registerApp(mBuilder.getAppId());
        if (!mWXAPI.isWXAppInstalled()) {
            finishWithError(new SocialLoginException(getPlatform(), ErrConstants.ERR_APP_NOT_INSTALL));
            return;
        }

        SendAuth.Req req = new SendAuth.Req();
        req.scope = mBuilder.getScope();
        req.state = mBuilder.getState();
        boolean sendReq = mWXAPI.sendReq(req);
        if (!sendReq) {
            finishWithError(new SocialLoginException(getPlatform(), ErrConstants.ERR_REQUEST_FAIL));
        }
    }

    @Override
    protected void finishImpl() {
        if (mWXLossResultWorkaround != null) {
            mWXLossResultWorkaround.setHaveResult(true);
            mWXLossResultWorkaround.stop();
        }
        if (mWXAPI != null) {
            mWXAPI.detach();
        }
    }

    @Override
    protected void doOnDelegateCreate(WeixinDelegateActivity weixinDelegateActivity) {

    }

    @Override
    public void handleResult(int requestCode, int resultCode, Intent data) {
        try {
            if (mWXLossResultWorkaround != null) {
                mWXLossResultWorkaround.setHaveResult(true);
                mWXLossResultWorkaround.stop();
            }
            PrintLog.d("Weixin", "req=" + requestCode + ", res=" + resultCode + ", data=" + data);
            mWXAPI.handleIntent(data, this);
        } catch (Exception e) {
            e.printStackTrace();
            finishWithError(e);
        }
    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp baseResp) {
        try {
            if (baseResp != null) {
                if (!(baseResp instanceof SendAuth.Resp)) {
                    finishWithCancel();
                    return;
                }
                final SendAuth.Resp resp = (SendAuth.Resp) baseResp;
                switch (resp.errCode) {
                    case 0:
                        SocialUtils.runOnBackground(new Runnable() {
                            @Override
                            public void run() {
                                doOnComplete(resp);
                            }
                        });
                        break;
                    case -2:
                    case -4:
                        PrintLog.e("SocialLogin", "Wechat, errCode=" + resp.errCode);
                        finishWithCancel();
                        break;
                    default:
                        PrintLog.e("SocialLogin", "Wechat, errCode=" + resp.errCode);
                        finishWithError(new SocialException(getPlatform(), ErrConstants.ERR_OTHER, resp.errCode, resp.errStr, resp));
                        break;
                }
            } else {
                finishWithError(new SocialLoginException(getPlatform(), ErrConstants.ERR_OTHER));
            }
        } catch (Exception e) {
            e.printStackTrace();
            finishWithError(e);
        }
    }

    private void doOnComplete(SendAuth.Resp resp) {
        if (mBuilder.isFetchUserProfile()) {
            fetchUserProfile(resp);
        } else {
            fetchBaseProfile(resp);
        }
    }

    private void fetchBaseProfile(SendAuth.Resp resp) {
        SocialLoginResult loginResult = new SocialLoginResult();
        loginResult.platform = getPlatform();
        loginResult.uid = resp.code;
        loginResult.resultObject = resp;
        finishWithSuccess(loginResult);
    }

    private void fetchUserProfile(SendAuth.Resp resp) {
        try {
            String accessTokenApi = getAccessTokenApi(resp.code);
            String accessTokenJson = SocialUtils.quickHttpGet(accessTokenApi);
            JSONObject jsonObject = new JSONObject(accessTokenJson);
            String access_token = jsonObject.optString("access_token");
            long expires_in = jsonObject.optLong("expires_in");
            String refresh_token = jsonObject.optString("refresh_token");
            String openid = jsonObject.optString("openid");
            String userInfoApi = getUserInfoApi(access_token, openid);
            String userInfoJsonString = SocialUtils.quickHttpGet(userInfoApi);
            JSONObject userJson = new JSONObject(userInfoJsonString);
            String nickname = userJson.optString("nickname");
            String sex = userJson.optString("sex");
            String headimgurl = userJson.optString("headimgurl");

            SocialLoginResult loginResult = new SocialLoginResult();
            loginResult.platform = getPlatform();

            AccessToken accessToken = new AccessToken();
            accessToken.accessToken = access_token;
            accessToken.refreshToken = refresh_token;
            accessToken.expiresIn = expires_in;
            accessToken.openId = openid;
            loginResult.accessToken = accessToken;

            UserInfo userInfo = new UserInfo();
            userInfo.nickname = (nickname);
            userInfo.gender = "1".equals(sex) ? 1 : "2".equals(sex) ? 0 : 2;
            userInfo.avatarUrl = headimgurl;
            loginResult.userInfo = userInfo;

            loginResult.resultObject = resp;
            finishWithSuccess(loginResult);
        } catch (Exception e) {
            e.printStackTrace();
            finishWithError(e);
        }
    }

    private String getAccessTokenApi(String code) {
        return "https://api.weixin.qq.com/sns/oauth2/access_token?"
            + "appid=" + mBuilder.getAppId()
            + "&secret=" + mBuilder.getAppSecret()
            + "&code=" + code
            + "&grant_type=authorization_code";
    }

    private String getUserInfoApi(String accessToken, String openId) {
        return "https://api.weixin.qq.com/sns/userinfo?access_token=" +
            accessToken + "&openid=" + openId + "&lang=zh_CN";
    }


    @Override
    public void onCallback() {
        SocialUtils.runOnBackground(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    if (mWXLossResultWorkaround != null && !mWXLossResultWorkaround.haveResult())
                        finishWithNoResult();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}

