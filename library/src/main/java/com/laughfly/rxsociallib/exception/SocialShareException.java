package com.laughfly.rxsociallib.exception;

import com.laughfly.rxsociallib.Platform;
import com.laughfly.rxsociallib.SocialConstants;

public class SocialShareException extends SocialException {

    public SocialShareException(Platform platform, @SocialConstants.ErrCode int errCode) {
        super(platform, errCode);
    }

    public SocialShareException(Platform platform, @SocialConstants.ErrCode int errCode, Object dataObject) {
        super(platform, errCode, dataObject);
    }

    public SocialShareException(Platform platform, @SocialConstants.ErrCode int errCode, int platformErrCode, String message, Object dataObject) {
        super(platform, errCode, platformErrCode, message, dataObject);
    }

    public SocialShareException(Platform platform, Throwable throwable) {
        super(platform, throwable);
    }
}
