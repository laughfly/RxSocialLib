package com.laughfly.rxsociallib.platform.wechat;

import android.content.Intent;
import android.support.annotation.MainThread;
import android.support.annotation.WorkerThread;

import com.laughfly.rxsociallib.AccessToken;
import com.laughfly.rxsociallib.SocialConstants;
import com.laughfly.rxsociallib.SocialUtils;
import com.laughfly.rxsociallib.exception.SocialException;
import com.laughfly.rxsociallib.exception.SocialLoginException;
import com.laughfly.rxsociallib.internal.AccessTokenKeeper;
import com.laughfly.rxsociallib.login.LoginAction;
import com.laughfly.rxsociallib.login.LoginFeature;
import com.laughfly.rxsociallib.login.LoginFeatures;
import com.laughfly.rxsociallib.login.LoginResult;
import com.laughfly.rxsociallib.login.UserInfo;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.json.JSONException;
import org.json.JSONObject;

import static com.laughfly.rxsociallib.SocialThreads.runOnThread;

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
    protected boolean useDelegate() {
        return false;
    }

    @Override
    protected void check() throws Exception {
        if (!SocialUtils.checkAppInstalled(mParams.getContext(), WechatConstants.WECHAT_PACKAGE)) {
            throw new SocialLoginException(getPlatform(), SocialConstants.ERR_APP_NOT_INSTALL);
        }
    }

    @Override
    protected void init() throws Exception {
        mWXAPI = WXAPIFactory.createWXAPI(mParams.getContext(), mParams.getAppId(), true);
        mWXAPI.registerApp(mParams.getAppId());
    }

    @Override
    protected void execute() throws Exception {
        if (mParams.isLogoutOnly()) {
            AccessTokenKeeper.clear(mParams.getContext(), getPlatform());
            finishWithLogout();
        } else {
            if (mParams.isClearLastAccount()) {
                AccessTokenKeeper.clear(mParams.getContext(), getPlatform());
            }
            loginSSO();
        }
    }

    @Override
    protected void release() throws Exception {
        if (mWXAPI != null) {
            mWXAPI.detach();
        }
        mWXAPI = null;
    }

    @Override
    public void handleResult(int requestCode, int resultCode, Intent data) throws Exception {
        mWXAPI.handleIntent(data, this);
    }

    private void loginSSO() throws SocialLoginException {
        WechatEntryActivity.setTheResultHandler(new ResultCallbackWrapper(this));
        SendAuth.Req req = new SendAuth.Req();
        req.scope = mParams.getScope();
        req.state = mParams.getState();
        boolean sendReq = mWXAPI.sendReq(req);
        if (!sendReq) {
            throw new SocialLoginException(getPlatform(), SocialConstants.ERR_REQUEST_FAIL);
        }
    }

    @Override
    public void onReq(BaseReq baseReq) {
    }

    @Override
    @MainThread
    public void onResp(BaseResp baseResp) {
        if (baseResp instanceof SendAuth.Resp) {
            runOnThread(new Runnable() {
                @Override
                public void run() {
                    handleResp((SendAuth.Resp) baseResp);
                }
            });
        } else {
            finishWithError(new SocialLoginException(getPlatform(), SocialConstants.ERR_OTHER));
        }
    }

    @WorkerThread
    private void handleResp(SendAuth.Resp resp) {
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                handleSuccess(resp);
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                finishWithCancel();
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                finishWithError(new SocialException(getPlatform(), SocialConstants.ERR_AUTH_DENIED, resp.errCode, resp.errStr, resp));
                break;
            default:
                finishWithError(new SocialException(getPlatform(), SocialConstants.ERR_OTHER, resp.errCode, resp.errStr, resp));
                break;
        }
    }

    @WorkerThread
    private void handleSuccess(SendAuth.Resp resp) {
        try {
            if (mParams.isServerSideMode()) {
                setServerAuthCode(resp);
            } else {
                setAccessToken(resp);
            }
        } catch (Exception e) {
            e.printStackTrace();
            finishWithError(e);
        }
    }

    private void setServerAuthCode(SendAuth.Resp resp) {
        LoginResult loginResult = new LoginResult();
        loginResult.platform = getPlatform();
        loginResult.serverAuthCode = resp.code;
        loginResult.openId = resp.openId;
        loginResult.resultObject = resp;
        finishWithSuccess(loginResult);
    }

    private void setAccessToken(SendAuth.Resp resp) throws JSONException {
        String accessTokenApi = getAccessTokenApi(resp.code);
        String accessTokenJson = SocialUtils.quickHttpGet(accessTokenApi);
        JSONObject jsonObject = new JSONObject(accessTokenJson);
        String access_token = jsonObject.optString("access_token");
        long expires_in = jsonObject.optLong("expires_in");
        String refresh_token = jsonObject.optString("refresh_token");
        String openId = jsonObject.optString("openid");

        AccessToken accessToken = new AccessToken();
        accessToken.accessToken = access_token;
        accessToken.expiresIn = expires_in;
        accessToken.openId = openId;
        accessToken.refreshToken = refresh_token;

        if(mParams.isSaveAccessToken()) {
            saveAccessToken(accessToken);
        }

        LoginResult result = new LoginResult();
        result.platform = getPlatform();
        result.openId = openId;
        result.accessToken = accessToken;
        result.resultObject = resp;

        if (mParams.isFetchUserProfile()) {
            String userInfoApi = getUserInfoApi(access_token, openId);
            String userInfoJsonString = SocialUtils.quickHttpGet(userInfoApi);
            JSONObject userJson = new JSONObject(userInfoJsonString);
            String nickname = userJson.optString("nickname");
            String sex = userJson.optString("sex");
            String headimgurl = userJson.optString("headimgurl");

            UserInfo userInfo = new UserInfo();
            userInfo.nickname = (nickname);
            userInfo.gender = "1".equals(sex) ? UserInfo.GENDER_MALE : "2".equals(sex) ? UserInfo.GENDER_FEMALE : UserInfo.GENDER_UNKNOWN;
            userInfo.avatarUrl = headimgurl;
            result.userInfo = userInfo;
        }

        finishWithSuccess(result);
    }

    private String getAccessTokenApi(String code) {
        return "https://api.weixin.qq.com/sns/oauth2/access_token?"
            + "appid=" + mParams.getAppId()
            + "&secret=" + mParams.getAppSecret()
            + "&code=" + code
            + "&grant_type=authorization_code";
    }

    private String getUserInfoApi(String accessToken, String openId) {
        return "https://api.weixin.qq.com/sns/userinfo?access_token=" +
            accessToken + "&openid=" + openId + "&lang=zh_CN";
    }

}

