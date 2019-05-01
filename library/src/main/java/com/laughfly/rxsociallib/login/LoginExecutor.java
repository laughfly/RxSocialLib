package com.laughfly.rxsociallib.login;

import com.laughfly.rxsociallib.SocialActionFactory;
import com.laughfly.rxsociallib.internal.SocialExecutor;

/**
 * Created by caowy on 2019/4/29.
 * email:cwy.fly2@gmail.com
 */

public class LoginExecutor extends SocialExecutor<LoginAction, SocialLoginResult>{
    private LoginBuilder mBuilder;

    public LoginExecutor(LoginBuilder builder) {
        mBuilder = builder;
    }

    protected LoginAction createAction() {
        return SocialActionFactory.createLoginAction(mBuilder.getPlatform(), mBuilder);
    }

}
