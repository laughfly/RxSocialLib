package com.laughfly.rxsociallib.exception;

import com.laughfly.rxsociallib.SocialConstants;

public class SocialException extends Exception {

    private String mPlatform;

    @SocialConstants.ErrCode
    private int mErrCode;

    private int mPlatformErrCode = 0;

    private Object mDataObject;

    public SocialException(String platform, @SocialConstants.ErrCode int errCode) {
        super();
        mPlatform = platform;
        mErrCode = errCode;
    }

    public SocialException(String platform, @SocialConstants.ErrCode int errCode, Object dataObject) {
        super();
        mPlatform = platform;
        mErrCode = errCode;
        mDataObject = dataObject;
    }

    public SocialException(String platform, @SocialConstants.ErrCode int errCode, int platformErrCode, String message, Object dataObject) {
        super(message);
        mPlatform = platform;
        mErrCode = errCode;
        mPlatformErrCode = platformErrCode;
        mDataObject = dataObject;
    }

    public SocialException(String platform, Throwable throwable) {
        super(throwable);
        mPlatform = platform;
        mErrCode = SocialConstants.ERR_EXCEPTION;
    }

    public int getErrCode() {
        return mErrCode;
    }

    public String getPlatform() {
        return mPlatform;
    }

    public int getPlatformErrCode() {
        return mPlatformErrCode;
    }

    public Object getDataObject() {
        return mDataObject;
    }
}
