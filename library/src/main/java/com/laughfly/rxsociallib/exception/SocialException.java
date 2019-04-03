package com.laughfly.rxsociallib.exception;

import com.laughfly.rxsociallib.ErrConstants;
import com.laughfly.rxsociallib.Platform;

public class SocialException extends Exception {

    private Platform mPlatform;

    @ErrConstants.ErrCode
    private int mErrCode;

    private int mPlatformErrCode = 0;

    private Object mDataObject;

    public SocialException(Platform platform, @ErrConstants.ErrCode int errCode) {
        super();
        mPlatform = platform;
        mErrCode = errCode;
    }

    public SocialException(Platform platform, @ErrConstants.ErrCode int errCode, Object dataObject) {
        super();
        mPlatform = platform;
        mErrCode = errCode;
        mDataObject = dataObject;
    }

    public SocialException(Platform platform, @ErrConstants.ErrCode int errCode, int platformErrCode, String message, Object dataObject) {
        super(message);
        mPlatform = platform;
        mErrCode = errCode;
        mPlatformErrCode = platformErrCode;
        mDataObject = dataObject;
    }

    public SocialException(Platform platform, Throwable throwable) {
        super(throwable);
        mPlatform = platform;
        mErrCode = ErrConstants.ERR_EXCEPTION;
    }

    public int getErrCode() {
        return mErrCode;
    }

    public Platform getPlatform() {
        return mPlatform;
    }

    public int getPlatformErrCode() {
        return mPlatformErrCode;
    }

    public Object getDataObject() {
        return mDataObject;
    }
}
