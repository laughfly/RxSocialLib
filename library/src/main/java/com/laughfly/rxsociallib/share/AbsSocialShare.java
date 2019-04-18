package com.laughfly.rxsociallib.share;


import com.laughfly.rxsociallib.SocialCallback;
import com.laughfly.rxsociallib.SocialConstants;
import com.laughfly.rxsociallib.SocialModel;
import com.laughfly.rxsociallib.SocialUriUtils;
import com.laughfly.rxsociallib.delegate.SocialActivity;
import com.laughfly.rxsociallib.exception.SocialShareException;
import com.laughfly.rxsociallib.internal.SocialAction;

import java.io.File;

/**
 * 分享的基础类
 * author:caowy
 * date:2018-04-20
 *
 * @param <Delegate>
 */
public abstract class AbsSocialShare<Delegate extends SocialActivity> extends SocialAction<ShareBuilder, Delegate, SocialShareResult> {

    public AbsSocialShare() {
        super();
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

    @Override
    protected void onStart() throws Exception {
        super.onStart();
        @ShareType.Def int type = getShareType();
        if(!isShareTypeSupport(type)) {
            throw new SocialShareException(getPlatform(), SocialConstants.ERR_SHARETYPE_NOT_SUPPORT);
        }
    }

    protected String downloadImageIfNeed(String imageUri) throws SocialShareException {
        try {
            if (SocialUriUtils.isHttpUrl(imageUri)) {
                File downloadFile = SocialModel.getImageDownloader().download(imageUri);
                imageUri = downloadFile.getAbsolutePath();
            }
            return imageUri;
        } catch (Exception e) {
            throw new SocialShareException(getPlatform(), SocialConstants.ERR_DOWNLOAD_FAILED, imageUri);
        }
    }

    protected boolean isShareTypeSupport(@ShareType.Def int shareType) {
        return (getSupportShareType() & shareType) != ShareType.SHARE_NONE;
    }

    protected @ShareType.Def int getSupportShareType() {
        ShareFeatures shareFeatures = getClass().getAnnotation(ShareFeatures.class);
        ShareFeature shareFeature = null;
        if(shareFeatures != null) {
            for (ShareFeature feature : shareFeatures.value()) {
                if(feature.platform().equalsIgnoreCase(getPlatform())) {
                    shareFeature = feature;
                    break;
                }
            }
        }
        return shareFeature != null ? shareFeature.supportFeatures() : ShareType.SHARE_NONE;
    }

    protected @ShareType.Def int getShareType() {
        ShareBuilder builder = getBuilder();
        if(builder.hasVideo()) {
            if(SocialUriUtils.isHttpUrl(builder.getVideoUri())) {
                return ShareType.SHARE_NETWORK_VIDEO;
            } else {
                return ShareType.SHARE_LOCAL_VIDEO;
            }
        }

        if(builder.hasAudio()) {
            return ShareType.SHARE_AUDIO;
        }

        if (builder.hasImage()) {
            return ShareType.SHARE_IMAGE;
        }

        if (builder.hasImageList()) {
            return ShareType.SHARE_MULTI_IMAGE;
        }

        if (builder.hasAppInfo()) {
            return ShareType.SHARE_APP;
        }

        if(builder.hasFileList()) {
            return ShareType.SHARE_MULTI_FILE;
        }

        if (builder.hasFilePath()) {
            return ShareType.SHARE_FILE;
        }

        if(builder.hasPageUrl()) {
            return ShareType.SHARE_WEB;
        }

        return ShareType.SHARE_TEXT;
    }
}
