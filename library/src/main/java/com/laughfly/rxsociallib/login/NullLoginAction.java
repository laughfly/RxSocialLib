package com.laughfly.rxsociallib.login;

import android.content.Intent;

import com.laughfly.rxsociallib.SocialConstants;
import com.laughfly.rxsociallib.exception.SocialLoginException;

/**
 * Created by caowy on 2019/4/30.
 * email:cwy.fly2@gmail.com
 */
public class NullLoginAction extends LoginAction {
    @Override
    protected void execute() throws Exception {
        throw new SocialLoginException(getPlatform(), SocialConstants.ERR_CREATE_ACTION_FAIL);
    }

    @Override
    protected void handleResult(int requestCode, int resultCode, Intent data) throws Exception {

    }
}
