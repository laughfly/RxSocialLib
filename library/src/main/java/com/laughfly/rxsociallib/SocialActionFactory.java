package com.laughfly.rxsociallib;

import com.laughfly.rxsociallib.login.AbsSocialLogin;
import com.laughfly.rxsociallib.login.LoginBuilder;
import com.laughfly.rxsociallib.share.AbsSocialShare;
import com.laughfly.rxsociallib.share.ShareBuilder;

/**
 * Created by caowy on 2019/4/11.
 * email:cwy.fly2@gmail.com
 */

public class SocialActionFactory {
    public static AbsSocialShare createShareAction(String platform, ShareBuilder builder) {
        try {
            Class<? extends AbsSocialShare> shareClass = SocialModel.getShareClass(platform);
            AbsSocialShare absSocialShare = shareClass.newInstance();
            absSocialShare.setBuilder(builder);
            return absSocialShare;
        } catch (NullPointerException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static AbsSocialLogin createLoginAction(String platform, LoginBuilder builder) {
        try {
            Class<? extends AbsSocialLogin> shareClass = SocialModel.getLoginClass(platform);
            AbsSocialLogin absSocialLogin = shareClass.newInstance();
            absSocialLogin.setBuilder(builder);
            return absSocialLogin;
        } catch (NullPointerException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
