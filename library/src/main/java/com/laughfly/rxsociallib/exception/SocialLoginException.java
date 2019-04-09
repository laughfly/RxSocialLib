package com.laughfly.rxsociallib.exception;

import com.laughfly.rxsociallib.Platform;
import com.laughfly.rxsociallib.SocialConstants;

public class SocialLoginException extends SocialException {

    public SocialLoginException(Platform platform, @SocialConstants.ErrCode int errCode) {
        super(platform, errCode);
    }

    public SocialLoginException(Platform platform, @SocialConstants.ErrCode int errCode, Object dataObject) {
        super(platform, errCode, dataObject);
    }

    public SocialLoginException(Platform platform, @SocialConstants.ErrCode int errCode, int platformErrCode, String message, Object dataObject) {
        super(platform, errCode, platformErrCode, message, dataObject);
    }

    public SocialLoginException(Platform platform, Throwable throwable) {
        super(platform, throwable);
    }
}