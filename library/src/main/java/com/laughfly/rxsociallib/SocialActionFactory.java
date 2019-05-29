package com.laughfly.rxsociallib;

import android.support.annotation.NonNull;

import com.laughfly.rxsociallib.login.LoginAction;
import com.laughfly.rxsociallib.login.LoginParams;
import com.laughfly.rxsociallib.login.NullLoginAction;
import com.laughfly.rxsociallib.share.NullShareAction;
import com.laughfly.rxsociallib.share.ShareAction;
import com.laughfly.rxsociallib.share.ShareParams;

/**
 * Created by caowy on 2019/4/11.
 * email:cwy.fly2@gmail.com
 */

public class SocialActionFactory {
    public static @NonNull ShareAction createShareAction(ShareParams params) {
        try {
            Class<? extends ShareAction> shareClass = SocialModel.getShareClass(params.getPlatform());
            ShareAction shareAction = shareClass.newInstance();
            shareAction.setParams(params);
            return shareAction;
        } catch (NullPointerException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return (ShareAction) new NullShareAction().setParams(params);
    }

    public static @NonNull LoginAction createLoginAction(LoginParams params) {
        try {
            Class<? extends LoginAction> loginClass = SocialModel.getLoginClass(params.getPlatform());
            LoginAction loginAction = loginClass.newInstance();
            loginAction.setParams(params);
            return loginAction;
        } catch (NullPointerException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return (LoginAction) new NullLoginAction().setParams(params);
    }
}
