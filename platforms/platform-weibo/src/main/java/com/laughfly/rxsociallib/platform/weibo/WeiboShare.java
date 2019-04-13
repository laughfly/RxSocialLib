package com.laughfly.rxsociallib.platform.weibo;

import android.content.Intent;
import android.graphics.Bitmap;

import com.laughfly.rxsociallib.SocialConstants;
import com.laughfly.rxsociallib.SocialThreads;
import com.laughfly.rxsociallib.SocialUtils;
import com.laughfly.rxsociallib.exception.SocialShareException;
import com.laughfly.rxsociallib.share.AbsSocialShare;
import com.laughfly.rxsociallib.share.ShareBuilder;
import com.laughfly.rxsociallib.share.ShareFeature;
import com.laughfly.rxsociallib.share.ShareFeatures;
import com.laughfly.rxsociallib.share.SocialShareResult;
import com.sina.weibo.sdk.WbSdk;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.share.WbShareCallback;
import com.sina.weibo.sdk.share.WbShareHandler;

/**
 * 微博分享
 * author:caowy
 * date:2018-05-26
 */
@ShareFeatures({
    @ShareFeature(platform = "Weibo", supportFeatures = ShareFeature.SHARE_TEXT)
})
public class WeiboShare extends AbsSocialShare<WeiboDelegateActivity> implements WbShareCallback {

    private WbShareHandler mWbShareHandler;

    public WeiboShare() {
        super();
    }

    @Override
    protected void startImpl() {
        if (!WbSdk.isWbInstall(mBuilder.getContext())) {
            finishWithError(new SocialShareException(getPlatform(), SocialConstants.ERR_APP_NOT_INSTALL));
            return;
        }

        WbSdk.install(mBuilder.getContext(),
            new AuthInfo(mBuilder.getContext(), mBuilder.getAppId(), mBuilder.getRedirectUrl(), mBuilder.getScope()));

        WeiboDelegateActivity.start(getContext(), WeiboShare.this);
    }

    @Override
    protected void finishImpl() {

    }

    @Override
    public void onDelegateCreate(final WeiboDelegateActivity activity) {
        super.onDelegateCreate(activity);
        SocialThreads.runOnThread(new Runnable() {
            @Override
            public void run() {
                try {
                    shareImpl(activity);
                } catch (Exception e) {
                    e.printStackTrace();
                    finishWithError(e);
                }
            }
        });
    }

    private void shareImpl(WeiboDelegateActivity activity) {
        mWbShareHandler = new WbShareHandler(activity);
        mWbShareHandler.registerApp();

        WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
        if (mBuilder.hasText()) {
            weiboMessage.textObject = createTextObject(mBuilder);
        }
        if (mBuilder.hasImage()) {
            weiboMessage.imageObject = createImageObj(mBuilder);
        }
        mWbShareHandler.shareMessage(weiboMessage, false);
    }

    private TextObject createTextObject(ShareBuilder builder) {
        TextObject textObject = new TextObject();
        textObject.text = builder.getTitle() != null ? builder.getTitle() : builder.getText() != null ?
            builder.getText() : builder.getExText();
        if (builder.getPageUrl() != null) {
            textObject.text = textObject.text + builder.getPageUrl();
        }
        return textObject;
    }

    private ImageObject createImageObj(ShareBuilder builder) {
        ImageObject imageObject = new ImageObject();
        Bitmap imageBitmap = builder.getImageBitmap();
        String imageUri = builder.getImageUri();
        if (imageBitmap != null) {
            imageObject.setImageObject(imageBitmap);
            return imageObject;
        } else if (imageUri != null) {
            imageObject.setImageObject(SocialUtils.loadBitmap(imageUri));
            return imageObject;
        } else {
            return null;
        }
    }

    @Override
    public void handleResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != 0) {
            SocialThreads.postOnUiThread(new Runnable() {
                @Override
                public void run() {
                    finishWithCancel();
                }
            }, WeiboShare.this, 100);
            return;
        }
        SocialThreads.removeUiRunnable(WeiboShare.this);
        try {
            mWbShareHandler.doResultIntent(data, this);
        } catch (Exception e) {
            e.printStackTrace();
            finishWithError(e);
        }
    }

    @Override
    public void onWbShareSuccess() {
        finishWithSuccess(new SocialShareResult(getPlatform()));
    }

    @Override
    public void onWbShareCancel() {
        finishWithCancel();
    }

    @Override
    public void onWbShareFail() {
        finishWithError(new SocialShareException(getPlatform(), SocialConstants.ERR_REQUEST_FAIL));
    }
}
