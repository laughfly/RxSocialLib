package com.laughfly.rxsociallib.platform.qq;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.laughfly.rxsociallib.Platform;
import com.laughfly.rxsociallib.SocialConstants;
import com.laughfly.rxsociallib.SocialThreads;
import com.laughfly.rxsociallib.SocialUtils;
import com.laughfly.rxsociallib.exception.SocialShareException;
import com.laughfly.rxsociallib.share.AbsSocialShare;
import com.laughfly.rxsociallib.share.ShareBuilder;
import com.laughfly.rxsociallib.share.SocialShareResult;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import static com.tencent.connect.share.QQShare.SHARE_TO_QQ_APP_NAME;
import static com.tencent.connect.share.QQShare.SHARE_TO_QQ_ARK_INFO;
import static com.tencent.connect.share.QQShare.SHARE_TO_QQ_AUDIO_URL;
import static com.tencent.connect.share.QQShare.SHARE_TO_QQ_EXT_INT;
import static com.tencent.connect.share.QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN;
import static com.tencent.connect.share.QQShare.SHARE_TO_QQ_FLAG_QZONE_ITEM_HIDE;
import static com.tencent.connect.share.QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL;
import static com.tencent.connect.share.QQShare.SHARE_TO_QQ_IMAGE_URL;
import static com.tencent.connect.share.QQShare.SHARE_TO_QQ_KEY_TYPE;
import static com.tencent.connect.share.QQShare.SHARE_TO_QQ_SUMMARY;
import static com.tencent.connect.share.QQShare.SHARE_TO_QQ_TARGET_URL;
import static com.tencent.connect.share.QQShare.SHARE_TO_QQ_TITLE;
import static com.tencent.connect.share.QQShare.SHARE_TO_QQ_TYPE_APP;
import static com.tencent.connect.share.QQShare.SHARE_TO_QQ_TYPE_AUDIO;
import static com.tencent.connect.share.QQShare.SHARE_TO_QQ_TYPE_DEFAULT;
import static com.tencent.connect.share.QQShare.SHARE_TO_QQ_TYPE_IMAGE;

/**
 * QQ分享，包括QQ好友和QZone
 * author:caowy
 * date:2018-05-11
 */
public class QQShare extends AbsSocialShare<QQDelegateActivity> implements IUiListener {

    private Tencent mTencent;

    public QQShare(ShareBuilder builder) {
        super(builder);
    }

    @Override
    protected void startImpl() {
        if (!SocialUtils.isQQInstalled(mBuilder.getContext())) {
            finishWithError(new SocialShareException(getPlatform(), SocialConstants.ERR_APP_NOT_INSTALL));
            return;
        }
        String appkey = mBuilder.getAppId();
        mTencent = Tencent.createInstance(appkey, mBuilder.getContext());
        QQDelegateActivity.start(getBuilder().getContext(), QQShare.this);
    }

    @Override
    protected void finishImpl() {
    }

    @Override
    public void onDelegateCreate(final QQDelegateActivity activity) {
        super.onDelegateCreate(activity);
        SocialThreads.runOnThread(new Runnable() {
            @Override
            public void run() {
                try {
                    shareToQQ(activity);
                } catch (Exception e) {
                    e.printStackTrace();
                    finishWithError(e);
                }
            }
        });
    }

    private void shareToQQ(QQDelegateActivity activity) {
        final Bundle params = new Bundle();

        String imageUri = mBuilder.getImageUri();
        String audioUri = mBuilder.getAudioUri();
        String arkInfo = mBuilder.getArkInfo();

        //分享图片
        boolean shareImage = SocialUtils.isLocalUri(imageUri);
        //分享音频
        boolean shareAudio = !TextUtils.isEmpty(audioUri);
        //分享app
        boolean shareApp = !TextUtils.isEmpty(arkInfo);

        int shareType;
        if (shareImage) {
            shareType = SHARE_TO_QQ_TYPE_IMAGE;
        } else if (shareAudio) {
            shareType = SHARE_TO_QQ_TYPE_AUDIO;
        } else if (shareApp) {
            shareType = SHARE_TO_QQ_TYPE_APP;
        } else {
            shareType = SHARE_TO_QQ_TYPE_DEFAULT;
        }

        params.putInt(SHARE_TO_QQ_KEY_TYPE, shareType);

        if (SHARE_TO_QQ_TYPE_IMAGE == shareType) {
            params.putString(SHARE_TO_QQ_IMAGE_LOCAL_URL, imageUri);
        } else {
            params.putString(SHARE_TO_QQ_TITLE, mBuilder.getTitle());
            params.putString(SHARE_TO_QQ_TARGET_URL, mBuilder.getPageUrl());
            params.putString(SHARE_TO_QQ_SUMMARY, mBuilder.getText());
            params.putString(SHARE_TO_QQ_IMAGE_URL, mBuilder.getThumbUri());
        }

        if (shareAudio) {
            params.putString(SHARE_TO_QQ_AUDIO_URL, audioUri);
        } else if(shareApp) {
            params.putString(SHARE_TO_QQ_ARK_INFO, arkInfo);
        }

        if (mBuilder.getAppName() != null) {
            params.putString(SHARE_TO_QQ_APP_NAME, mBuilder.getAppName());
        }

        if (getPlatform() == Platform.QQZone) {
            params.putInt(SHARE_TO_QQ_EXT_INT, SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN);
        } else {
            params.putInt(SHARE_TO_QQ_EXT_INT, SHARE_TO_QQ_FLAG_QZONE_ITEM_HIDE);
        }

        mTencent.shareToQQ(activity, params, QQShare.this);
    }

    @Override
    public void handleResult(int requestCode, int resultCode, Intent data) {
        try {
            //分享成功停留在QQ，并直接通过任务管理切换回APP
            if (requestCode == 0 && 0 == resultCode && data == null) {
                finishWithNoResult();
            } else {
                Tencent.onActivityResultData(requestCode, resultCode, data, QQShare.this);
            }
        } catch (Exception e) {
            e.printStackTrace();
            finishWithError(e);
        }
    }

    @Override
    public void onComplete(Object o) {
        SocialShareResult result = new SocialShareResult(getPlatform());
        finishWithSuccess(result);
    }

    @Override
    public void onError(UiError uiError) {
        @SocialConstants.ErrCode
        int errorCode;
        String msg = uiError != null ? uiError.errorMessage : "";
        int platformErrCode = uiError != null ? uiError.errorCode : -1;
        if (uiError == null) {
            errorCode = SocialConstants.ERR_OTHER;
        } else {
            switch (platformErrCode) {
                case 110404://没有传入AppId
                case 110407://应用授权已下架
                case 110406://授权未通过
                case 100044://签名错误
                case 110503://获取授权码失败
                    errorCode = SocialConstants.ERR_AUTH_DENIED;
                    break;
                case 110401://应用未安装
                    errorCode = SocialConstants.ERR_APP_NOT_INSTALL;
                    break;
                default://其他错误
                    errorCode = SocialConstants.ERR_OTHER;
                    break;
            }
        }
        finishWithError(new SocialShareException(getPlatform(), errorCode, platformErrCode, msg, uiError));
    }

    @Override
    public void onCancel() {
        finishWithCancel();
    }
}
