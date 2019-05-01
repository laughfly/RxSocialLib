package com.laughfly.rxsociallib;

import android.support.annotation.NonNull;

import com.laughfly.rxsociallib.login.LoginAction;
import com.laughfly.rxsociallib.login.LoginBuilder;
import com.laughfly.rxsociallib.login.NullLoginAction;
import com.laughfly.rxsociallib.share.NullShareAction;
import com.laughfly.rxsociallib.share.ShareAction;
import com.laughfly.rxsociallib.share.ShareBuilder;

/**
 * Created by caowy on 2019/4/11.
 * email:cwy.fly2@gmail.com
 */

public class SocialActionFactory {
    public static @NonNull ShareAction createShareAction(String platform, ShareBuilder builder) {
        try {
            Class<? extends ShareAction> shareClass = SocialModel.getShareClass(platform);
            ShareAction shareAction = shareClass.newInstance();
            shareAction.setBuilder(builder);
            return shareAction;
        } catch (NullPointerException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return (ShareAction) new NullShareAction().setBuilder(builder);
    }

    public static @NonNull LoginAction createLoginAction(String platform, LoginBuilder builder) {
        try {
            Class<? extends LoginAction> loginClass = SocialModel.getLoginClass(platform);
            LoginAction loginAction = loginClass.newInstance();
            loginAction.setBuilder(builder);
            return loginAction;
        } catch (NullPointerException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return (LoginAction) new NullLoginAction().setBuilder(builder);
    }
}
