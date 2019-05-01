package com.laughfly.rxsociallib.platform.wechat;

import android.content.Intent;

import com.laughfly.rxsociallib.AccessToken;
import com.laughfly.rxsociallib.SocialConstants;
import com.laughfly.rxsociallib.SocialLogger;
import com.laughfly.rxsociallib.SocialThreads;
import com.laughfly.rxsociallib.SocialUtils;
import com.laughfly.rxsociallib.exception.SocialException;
import com.laughfly.rxsociallib.exception.SocialLoginException;
import com.laughfly.rxsociallib.login.LoginAction;
import com.laughfly.rxsociallib.login.LoginFeature;
import com.laughfly.rxsociallib.login.LoginFeatures;
import com.laughfly.rxsociallib.login.SocialLoginResult;
import com.laughfly.rxsociallib.login.UserInfo;
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
@LoginFeatures({
    @LoginFeature(platform = "Wechat")
})
public class WechatLogin extends LoginAction implements IWXAPIEventHandler {

    private IWXAPI mWXAPI;

    @Override
    protected void check() throws Exception {
        if (!SocialUtils.checkAppInstalled(mBuilder.getContext(), WechatConstants.WECHAT_PACKAGE)) {
            throw new SocialLoginException(getPlatform(), SocialConstants.ERR_APP_NOT_INSTALL);
        }
    }

    @Override
    protected void init() throws Exception {
        mWXAPI = WXAPIFactory.createWXAPI(mBuilder.getContext(), mBuilder.getAppId(), true);
        mWXAPI.registerApp(mBuilder.getAppId());
    }

    @Override
    protected void execute() throws Exception {
        WechatEntryActivity.setTheResultHandler(new ResultCallbackWrapper(this));
        SendAuth.Req req = new SendAuth.Req();
        req.scope = mBuilder.getScope();
        req.state = mBuilder.getState();
        boolean sendReq = mWXAPI.sendReq(req);
        if (!sendReq) {
            throw new SocialLoginException(getPlatform(), SocialConstants.ERR_REQUEST_FAIL);
        }
    }

    @Override
    protected void release() throws Exception {
        if(mWXAPI != null) {
            mWXAPI.detach();
        }
        mWXAPI = null;
    }

    @Override
    public void handleResult(int requestCode, int resultCode, Intent data) throws Exception {
        mWXAPI.handleIntent(data, this);
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
                        SocialThreads.runOnThread(new Runnable() {
                            @Override
                            public void run() {
                                doOnComplete(resp);
                            }
                        });
                        break;
                    case -2:
                    case -4:
                        SocialLogger.e("SocialLogin", "Wechat, errCode=" + resp.errCode);
                        finishWithCancel();
                        break;
                    default:
                        SocialLogger.e("SocialLogin", "Wechat, errCode=" + resp.errCode);
                        finishWithError(new SocialException(getPlatform(), SocialConstants.ERR_OTHER, resp.errCode, resp.errStr, resp));
                        break;
                }
            } else {
                finishWithError(new SocialLoginException(getPlatform(), SocialConstants.ERR_OTHER));
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

}

