package com.laughfly.rxsociallib.platform.wechat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.text.TextUtils;

import com.laughfly.rxsociallib.SocialConstants;
import com.laughfly.rxsociallib.SocialIntentUtils;
import com.laughfly.rxsociallib.SocialThreads;
import com.laughfly.rxsociallib.SocialUriUtils;
import com.laughfly.rxsociallib.SocialUtils;
import com.laughfly.rxsociallib.delegate.DelegateHelper;
import com.laughfly.rxsociallib.delegate.SocialDelegateActivity;
import com.laughfly.rxsociallib.exception.SocialException;
import com.laughfly.rxsociallib.exception.SocialShareException;
import com.laughfly.rxsociallib.share.AbsSocialShare;
import com.laughfly.rxsociallib.share.ShareFeature;
import com.laughfly.rxsociallib.share.ShareFeatures;
import com.laughfly.rxsociallib.share.ShareType;
import com.laughfly.rxsociallib.share.SocialShareResult;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXMiniProgramObject;
import com.tencent.mm.opensdk.modelmsg.WXMusicObject;
import com.tencent.mm.opensdk.modelmsg.WXTextObject;
import com.tencent.mm.opensdk.modelmsg.WXVideoObject;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

/**
 * 微信分享
 * author:caowy
 * date:2018-05-26
 */
@ShareFeatures({
    @ShareFeature(platform = WechatConstants.WECHAT, supportFeatures = WechatConstants.WECHAT_SHARE_SUPPORT),
    @ShareFeature(platform = WechatConstants.WECHAT_MOMENTS, supportFeatures = WechatConstants.WECHAT_MOMENTS_SHARE_SUPPORT)
})
public class WechatShare extends AbsSocialShare<SocialDelegateActivity> implements IWXAPIEventHandler{

    private IWXAPI mWXApi;

    private boolean mShareByIntent;

    public WechatShare() {
        super();
    }

    @Override
    protected void startImpl() {
        if (mWXApi == null) {
            String appId = mBuilder.getAppId();
            mWXApi = WXAPIFactory.createWXAPI(getContext(), appId, true);
            mWXApi.registerApp(appId);
        }
        if (!mWXApi.isWXAppInstalled()) {
            finishWithError(new SocialShareException(getPlatform(), SocialConstants.ERR_APP_NOT_INSTALL));
            return;
        }

        WechatEntryActivity.setTheResultHandler(WechatShare.this);
        DelegateHelper.startActivity(mBuilder.getContext(), WechatDelegateActivity.class, WechatShare.this);
    }

    @Override
    public void onDelegateCreate(SocialDelegateActivity delegateActivity) {
        super.onDelegateCreate(delegateActivity);
        SocialThreads.runOnThread(new Runnable() {
            @Override
            public void run() {
                try {
                    shareToWechat();
                } catch (Exception e) {
                    e.printStackTrace();
                    finishWithError(e);
                }
            }
        });
    }

    private void shareToWechat() throws SocialShareException {
        int shareType = getShareType();
        switch (shareType) {
            case ShareType.SHARE_TEXT:
                shareText();
                break;
            case ShareType.SHARE_WEB:
                shareWebPage();
                break;
            case ShareType.SHARE_IMAGE:
                shareImage();
                break;
            case ShareType.SHARE_AUDIO:
                shareAudio();
                break;
            case ShareType.SHARE_FILE:
                shareFile();
                break;
            case ShareType.SHARE_LOCAL_VIDEO:
            case ShareType.SHARE_NETWORK_VIDEO:
                shareVideo();
                break;
            case ShareType.SHARE_MINI_PROGRAM:
                shareMiniProgram();
                break;
            case ShareType.SHARE_APP:
            case ShareType.SHARE_MULTI_FILE:
            case ShareType.SHARE_MULTI_IMAGE:
            case ShareType.SHARE_NONE:
                break;

        }
    }

    private void shareText() {
        WXTextObject textObject = new WXTextObject();
        textObject.text = mBuilder.getTitle();
        WXMediaMessage mediaMessage = new WXMediaMessage();
        mediaMessage.mediaObject = textObject;
        mediaMessage.title = mBuilder.getTitle();
        mediaMessage.description = mBuilder.getText();
        mediaMessage.messageExt = mBuilder.getExText();

        shareBySDK(mediaMessage);
    }

    private void shareWebPage() throws SocialShareException {
        WXWebpageObject webpageObject = new WXWebpageObject();
        webpageObject.webpageUrl = mBuilder.getWebUrl();
        WXMediaMessage mediaMessage = new WXMediaMessage();
        mediaMessage.mediaObject = webpageObject;
        mediaMessage.title = mBuilder.getTitle();
        mediaMessage.description = mBuilder.getText();
        mediaMessage.messageExt = mBuilder.getExText();
        mediaMessage.thumbData = createThumbData(WechatConstants.WECHAT_THUMB_LIMIT);

        shareBySDK(mediaMessage);
    }

    /**
     * 分享音频
     */
    private void shareAudio() throws SocialShareException {
        WXMusicObject musicObject = new WXMusicObject();
        musicObject.musicDataUrl = mBuilder.getAudioUri();
        musicObject.musicUrl = mBuilder.getWebUrl();

        WXMediaMessage mediaMessage = new WXMediaMessage();
        mediaMessage.title = mBuilder.getTitle();
        mediaMessage.description = mBuilder.getText();
        mediaMessage.mediaObject = musicObject;
        mediaMessage.thumbData = createThumbData(WechatConstants.WECHAT_THUMB_LIMIT);

        shareBySDK(mediaMessage);
    }

    /**
     * 分享图片
     *
     * @return
     */
    private void shareImage() throws SocialShareException {
        WXImageObject imageObject = new WXImageObject();

        WXMediaMessage mediaMessage = new WXMediaMessage();
        mediaMessage.mediaObject = imageObject;
        Bitmap imageBitmap = mBuilder.getImageBitmap();
        String imageUri = downloadImageIfNeed(mBuilder.getImageUri());
        if (imageBitmap != null) {
            imageObject.imageData = SocialUtils.bitmapToBytes(imageBitmap, WechatConstants.WECHAT_IMAGE_LIMIT);
        } else {
            imageObject.imageData = SocialUtils.loadImageBytes(imageUri, WechatConstants.WECHAT_IMAGE_LIMIT);
        }
        mediaMessage.thumbData = SocialUtils.scaleImage(imageObject.imageData, WechatConstants.WECHAT_THUMB_LIMIT);

        shareBySDK(mediaMessage);
    }

    private void shareVideo() throws SocialShareException {
        String videoUri = mBuilder.getVideoUri();
        if(SocialUriUtils.isHttpUrl(videoUri)) {
            WXVideoObject videoObject = new WXVideoObject();
            videoObject.videoUrl = mBuilder.getVideoUri();

            WXMediaMessage mediaMessage = new WXMediaMessage();
            mediaMessage.mediaObject = videoObject;
            mediaMessage.title = mBuilder.getTitle();
            mediaMessage.description = mBuilder.getText();
            mediaMessage.thumbData = createThumbData(WechatConstants.WECHAT_THUMB_LIMIT);

            shareBySDK(mediaMessage);
        } else {
            Intent fileShare = SocialIntentUtils.createVideoShare(Uri.parse(videoUri), WechatConstants.WECHAT_PACKAGE, WechatConstants.WECHAT_SHARE_TARGET_CLASS);

            shareByIntent(fileShare);
        }
    }

    private void shareFile() {
        final Intent fileShare = SocialIntentUtils.createFileShare(Uri.parse(mBuilder.getFileUri()), WechatConstants.WECHAT_PACKAGE, WechatConstants.WECHAT_SHARE_TARGET_CLASS);

        shareByIntent(fileShare);
    }

    /**
     * 分享小程序页面
     *
     * @return
     */
    private void shareMiniProgram() throws SocialShareException {
        WXMiniProgramObject programObject = new WXMiniProgramObject();
        programObject.webpageUrl = mBuilder.getWebUrl();
        programObject.userName = mBuilder.getMiniProgramUserName();
        programObject.path = mBuilder.getMiniProgramPath();
        programObject.miniprogramType = mBuilder.getMiniProgramType();

        WXMediaMessage mediaMessage = new WXMediaMessage(programObject);
        mediaMessage.title = mBuilder.getTitle();
        mediaMessage.description = mBuilder.getText();
        mediaMessage.thumbData = createThumbData(WechatConstants.WECHAT_MINI_PROG_IMAGE_LIMIT);

        shareBySDK(mediaMessage);
    }


    private void shareBySDK(WXMediaMessage mediaMessage) {
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = "" + System.currentTimeMillis();
        req.message = mediaMessage;
        req.scene = WechatConstants.WECHAT_MOMENTS.equalsIgnoreCase(getPlatform()) ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;
        boolean sendReq = mWXApi.sendReq(req);
        if (!sendReq) {
            finishWithError(new SocialShareException(getPlatform(), SocialConstants.ERR_REQUEST_FAIL));
        }
    }

    private void shareByIntent(final Intent intent) {
        SocialThreads.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    getContext().startActivity(intent);
                    mShareByIntent = true;
                } catch (Exception e) {
                    e.printStackTrace();
                    finishWithError(e);
                }
            }
        });
    }

    private byte[] createThumbData(int limit) throws SocialShareException {
        byte[] thumbData = null;
        Bitmap thumbBitmap = mBuilder.getThumbBitmap();
        if (thumbBitmap != null) {
            thumbData = SocialUtils.bitmapToBytes(thumbBitmap, limit);
        }
        if (thumbData == null) {
            int thumbResId = mBuilder.getThumbResId();
            if (thumbResId != 0) {
                Bitmap bitmap = BitmapFactory.decodeResource(mBuilder.getContext().getResources(), thumbResId);
                thumbData = SocialUtils.bitmapToBytes(bitmap, limit);
            }
        }
        if (thumbData == null) {
            String thumbUri = downloadImageIfNeed(mBuilder.getThumbUri());
            if (!TextUtils.isEmpty(thumbUri)) {
                thumbData = SocialUtils.loadImageBytes(thumbUri, limit);
            }
        }
        return thumbData;
    }

    @Override
    protected void finishImpl() {
        if (mWXApi != null) {
            mWXApi.detach();
        }
    }

    @Override
    public void handleResult(int requestCode, int resultCode, Intent data) {
        super.handleResult(requestCode, resultCode, data);
        try {
            if (data == null) {
                finishWithNoResult();
            } else {
                mWXApi.handleIntent(data, this);
            }
        } catch (Exception e) {
            e.printStackTrace();
            finishWithError(e);
        }
    }

    @Override
    public void handleNoResult() {
        if(mShareByIntent) {
            finishWithSuccess(new SocialShareResult(getPlatform()));
        } else {
            super.handleNoResult();
        }
    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp baseResp) {
        switch (baseResp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                finishWithSuccess(new SocialShareResult(getPlatform()));
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                finishWithCancel();
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                finishWithError(new SocialException(getPlatform(), SocialConstants.ERR_AUTH_DENIED, baseResp.errCode, baseResp.errStr, baseResp));
                break;
            default:
                finishWithError(new SocialException(getPlatform(), SocialConstants.ERR_OTHER, baseResp.errCode, baseResp.errStr, baseResp));
                break;
        }
    }

}
