package com.laughfly.rxsociallib.login;

import com.laughfly.rxsociallib.SocialCallback;
import com.laughfly.rxsociallib.SocialConstants;
import com.laughfly.rxsociallib.delegate.SocialActivity;
import com.laughfly.rxsociallib.exception.SocialLoginException;
import com.laughfly.rxsociallib.internal.SocialAction;

public abstract class AbsSocialLogin<Delegate extends SocialActivity> extends SocialAction<LoginBuilder, Delegate, SocialLoginResult>{

    public AbsSocialLogin() {
        super();
    }

    @Override
    public AbsSocialLogin setCallback(SocialCallback<SocialLoginResult> callback) {
        super.setCallback(callback);
        return this;
    }

    @Override
    protected void finishWithCancel() {
        finishWithError(new SocialLoginException(getPlatform(), SocialConstants.ERR_USER_CANCEL));
    }

    @Override
    protected void finishWithNoResult() {
        finishWithError(new SocialLoginException(getPlatform(), SocialConstants.ERR_NO_RESULT));
    }

    @Override
    protected void finishWithError(Exception e) {
        finishWithError(new SocialLoginException(getPlatform(), e));
    }
}
