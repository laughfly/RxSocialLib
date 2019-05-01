package com.laughfly.rxsociallib.share;

import android.content.Intent;

import com.laughfly.rxsociallib.SocialConstants;
import com.laughfly.rxsociallib.exception.SocialShareException;

/**
 * Created by caowy on 2019/4/30.
 * email:cwy.fly2@gmail.com
 */
public class NullShareAction extends ShareAction {
    @Override
    protected void execute() throws Exception {
        throw new SocialShareException(getPlatform(), SocialConstants.ERR_CREATE_ACTION_FAIL);
    }

    @Override
    protected void handleResult(int requestCode, int resultCode, Intent data) throws Exception {

    }
}
