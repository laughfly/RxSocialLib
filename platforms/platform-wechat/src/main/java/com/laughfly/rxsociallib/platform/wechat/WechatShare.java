package com.laughfly.rxsociallib.platform.wechat;

import android.content.Intent;
import android.net.Uri;

import com.laughfly.rxsociallib.SocialConstants;
import com.laughfly.rxsociallib.SocialIntentUtils;
import com.laughfly.rxsociallib.SocialThreads;
import com.laughfly.rxsociallib.SocialUriUtils;
import com.laughfly.rxsociallib.SocialUtils;
import com.laughfly.rxsociallib.exception.SocialException;
import com.laughfly.rxsociallib.exception.SocialLoginException;
import com.laughfly.rxsociallib.exception.SocialShareException;
import com.laughfly.rxsociallib.share.ShareAction;
import com.laughfly.rxsociallib.share.ShareFeature;
import com.laughfly.rxsociallib.share.ShareFeatures;
import com.laughfly.rxsociallib.share.ShareResult;
import com.laughfly.rxsociallib.share.ShareType;
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
public class WechatShare extends ShareAction implements IWXAPIEventHandler {

    private IWXAPI mWXApi;

    private boolean mShareByIntent;

    @Override
    protected void check() throws Exception {
        if (!SocialUtils.checkAppInstalled(mParams.getContext(), WechatConstants.WECHAT_PACKAGE)) {
            throw new SocialLoginException(getPlatform(), SocialConstants.ERR_APP_NOT_INSTALL);
        }
    }

    @Override
    protected void init() throws Exception {
        mWXApi = WXAPIFactory.createWXAPI(getContext(), mParams.getAppId(), true);
        mWXApi.registerApp(mParams.getAppId());
    }

    @Override
    protected void release() throws Exception {
        if (mWXApi != null) {
            mWXApi.detach();
        }
        mWXApi = null;
    }


    @Override
    public void handleResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            finishWithNoResult();
        } else {
            mWXApi.handleIntent(data, this);
        }
    }

    @Override
    public void handleNoResult() {
        if (mShareByIntent) {
            finishWithSuccess(new ShareResult(getPlatform()));
        } else {
            super.handleNoResult();
        }
    }

    @Override
    protected void execute() throws Exception {
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
        textObject.text = mParams.getText();
        WXMediaMessage mediaMessage = new WXMediaMessage();
        mediaMessage.mediaObject = textObject;
        mediaMessage.title = mParams.getTitle();
        mediaMessage.description = mParams.getText();
        mediaMessage.messageExt = mParams.getExText();

        shareBySDK(mediaMessage);
    }

    private void shareWebPage() throws SocialShareException {
        WXWebpageObject webpageObject = new WXWebpageObject();
        webpageObject.webpageUrl = mParams.getWebUrl();
        WXMediaMessage mediaMessage = new WXMediaMessage();
        mediaMessage.mediaObject = webpageObject;
        mediaMessage.title = mParams.getTitle();
        mediaMessage.description = mParams.getText();
        mediaMessage.messageExt = mParams.getExText();
        mediaMessage.thumbData = getThumbBytes(WechatConstants.THUMB_SIZE_LIMIT);

        shareBySDK(mediaMessage);
    }

    /**
     * 分享音频
     */
    private void shareAudio() throws SocialShareException {
        WXMusicObject musicObject = new WXMusicObject();
        musicObject.musicDataUrl = mParams.getAudioUri();
        musicObject.musicUrl = mParams.getWebUrl();

        WXMediaMessage mediaMessage = new WXMediaMessage();
        mediaMessage.title = mParams.getTitle();
        mediaMessage.description = mParams.getText();
        mediaMessage.mediaObject = musicObject;
        mediaMessage.thumbData = getThumbBytes(WechatConstants.THUMB_SIZE_LIMIT);

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
        imageObject.imagePath = getImagePath(WechatConstants.IMAGE_SIZE_LIMIT);
        mediaMessage.thumbData = SocialUtils.loadImageBytes(imageObject.imagePath, WechatConstants.THUMB_SIZE_LIMIT);

        shareBySDK(mediaMessage);
    }

    private void shareVideo() throws SocialShareException {
        String videoUri = mParams.getVideoUri();
        if (SocialUriUtils.isHttpUrl(videoUri)) {
            WXVideoObject videoObject = new WXVideoObject();
            videoObject.videoUrl = mParams.getVideoUri();

            WXMediaMessage mediaMessage = new WXMediaMessage();
            mediaMessage.mediaObject = videoObject;
            mediaMessage.title = mParams.getTitle();
            mediaMessage.description = mParams.getText();
            mediaMessage.thumbData = getThumbBytes(WechatConstants.THUMB_SIZE_LIMIT);

            shareBySDK(mediaMessage);
        } else {
            Intent fileShare = SocialIntentUtils.createVideoShare(Uri.parse(videoUri), WechatConstants.WECHAT_PACKAGE, WechatConstants.WECHAT_SHARE_TARGET_CLASS);

            shareByIntent(fileShare);
        }
    }

    private void shareFile() {
        final Intent fileShare = SocialIntentUtils.createFileShare(Uri.parse(mParams.getFileUri()), WechatConstants.WECHAT_PACKAGE, WechatConstants.WECHAT_SHARE_TARGET_CLASS);

        shareByIntent(fileShare);
    }

    /**
     * 分享小程序页面
     *
     * @return
     */
    private void shareMiniProgram() throws SocialShareException {
        WXMiniProgramObject programObject = new WXMiniProgramObject();
        programObject.webpageUrl = mParams.getWebUrl();
        programObject.userName = mParams.getMiniProgramUserName();
        programObject.path = mParams.getMiniProgramPath();
        programObject.miniprogramType = mParams.getMiniProgramType();

        WXMediaMessage mediaMessage = new WXMediaMessage(programObject);
        mediaMessage.title = mParams.getTitle();
        mediaMessage.description = mParams.getText();
        mediaMessage.thumbData = getThumbBytes(WechatConstants.MINI_PROG_IMAGE_LIMIT);

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

    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp baseResp) {
        switch (baseResp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                finishWithSuccess(new ShareResult(getPlatform()));
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
