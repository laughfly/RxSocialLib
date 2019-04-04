package com.laughfly.rxsociallib.share;


import com.laughfly.rxsociallib.SocialCallback;
import com.laughfly.rxsociallib.SocialConstants;
import com.laughfly.rxsociallib.delegate.SocialActivity;
import com.laughfly.rxsociallib.exception.SocialShareException;
import com.laughfly.rxsociallib.internal.SocialAction;

/**
 * 分享的基础类
 * author:caowy
 * date:2018-04-20
 *
 * @param <Delegate>
 */
public abstract class AbsSocialShare<Delegate extends SocialActivity> extends SocialAction<ShareBuilder, Delegate, SocialShareResult> {

    public AbsSocialShare(ShareBuilder builder) {
        super(builder);
    }

    @Override
    public AbsSocialShare setCallback(SocialCallback<SocialShareResult> callback) {
        super.setCallback(callback);
        return this;
    }

    @Override
    protected void finishWithCancel() {
        finishWithError(new SocialShareException(getPlatform(), SocialConstants.ERR_USER_CANCEL));
    }

    @Override
    protected void finishWithNoResult() {
        finishWithError(new SocialShareException(getPlatform(), SocialConstants.ERR_NO_RESULT));
    }

    @Override
    protected void finishWithError(Exception e) {
        finishWithError(new SocialShareException(getPlatform(), e));
    }
}
