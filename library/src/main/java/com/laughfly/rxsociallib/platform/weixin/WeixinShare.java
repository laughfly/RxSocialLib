package com.laughfly.rxsociallib.platform.weixin;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import com.laughfly.rxsociallib.ErrConstants;
import com.laughfly.rxsociallib.Logger;
import com.laughfly.rxsociallib.Platform;
import com.laughfly.rxsociallib.SocialConfig;
import com.laughfly.rxsociallib.SocialThreads;
import com.laughfly.rxsociallib.SocialUtils;
import com.laughfly.rxsociallib.exception.SocialException;
import com.laughfly.rxsociallib.exception.SocialShareException;
import com.laughfly.rxsociallib.share.AbsSocialShare;
import com.laughfly.rxsociallib.share.ShareBuilder;
import com.laughfly.rxsociallib.share.SocialShareResult;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXFileObject;
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

import static android.text.TextUtils.isEmpty;

/**
 * 微信分享
 * author:caowy
 * date:2018-05-26
 */
public class WeixinShare extends AbsSocialShare<WeixinDelegateActivity> implements IWXAPIEventHandler
    , WXLossResultWorkaround.Callback {

    private IWXAPI mWXApi;

    /**
     * 对没有返回结果的特殊处理
     */
    private WXLossResultWorkaround mWXLossResultWorkaround;

    public WeixinShare(ShareBuilder builder) {
        super(builder);
    }

    @Override
    protected void startImpl() {
        mWXLossResultWorkaround = new WXLossResultWorkaround(mBuilder.getContext(), this);
        mWXLossResultWorkaround.start();
        if (mWXApi == null) {
            String appId = mBuilder.getAppId();
            mWXApi = WXAPIFactory.createWXAPI(getContext(), appId, true);
            mWXApi.registerApp(appId);
        }
        if (!mWXApi.isWXAppInstalled()) {
            finishWithError(new SocialShareException(getPlatform(), ErrConstants.ERR_APP_NOT_INSTALL));
            return;
        }
        WeixinDelegateActivity.setTheResultHandler(WeixinShare.this);
        SocialThreads.runOnThread(new Runnable() {
            @Override
            public void run() {
                shareImpl();
            }
        });
    }

    private void shareImpl() {
        WXMediaMessage mediaMessage;
        if (mBuilder.getMiniProgramPath() != null) {//小程序
            mediaMessage = createMiniProgramObject();
        } else if (mBuilder.getAudioUri() != null) {//音频
            mediaMessage = createAudioObject();
        } else if (!isEmpty(mBuilder.getPageUrl())) {//网页
            mediaMessage = createWebpageObject();
        } else if (mBuilder.hasImage()) {//图片
            mediaMessage = createImageObject();
        } else if(!isEmpty(mBuilder.getVideoUrl())){
            mediaMessage = createVideoObject();
        } else if(!isEmpty(mBuilder.getFilePath())){
            mediaMessage = createFileObject();
        }else {//文字
            mediaMessage = createTextObject();
        }
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = "" + System.currentTimeMillis();
        req.message = mediaMessage;
        req.scene = mBuilder.getPlatform() == Platform.WEIXIN_MOMENTS ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;
        boolean sendReq = mWXApi.sendReq(req);
        if (!sendReq) {
            finishWithError(new SocialShareException(getPlatform(), ErrConstants.ERR_REQUEST_FAIL));
        }

    }

    /**
     * 分享音频
     */
    private WXMediaMessage createAudioObject() {
        WXMusicObject musicObject = new WXMusicObject();
        musicObject.musicDataUrl = mBuilder.getAudioUri();
        musicObject.musicUrl = mBuilder.getPageUrl();

        WXMediaMessage mediaMessage = new WXMediaMessage();
        mediaMessage.title = mBuilder.getTitle();
        mediaMessage.description = mBuilder.getText();
        mediaMessage.mediaObject = musicObject;
        mediaMessage.thumbData = createThumbData(SocialConfig.WECHAT_THUMB_LIMIT);
        return mediaMessage;
    }

    /**
     * 分享文本
     * author:caowy
     * date:2018-11-22
     *
     * @return
     */
    private WXMediaMessage createTextObject() {
        WXTextObject textObject = new WXTextObject();
        String title = mBuilder.getTitle();
        String text = mBuilder.getText();
        String exText = mBuilder.getExText();
        textObject.text = title != null ? title : title;
        WXMediaMessage mediaMessage = new WXMediaMessage();
        mediaMessage.mediaObject = textObject;
        mediaMessage.title = title != null ? title : text;
        mediaMessage.description = text;
        mediaMessage.messageExt = exText;
        return mediaMessage;
    }

    /**
     * 分享图片
     *
     * @return
     */
    private WXMediaMessage createImageObject() {
        WXImageObject imageObject = new WXImageObject();

        WXMediaMessage mediaMessage = new WXMediaMessage();
        mediaMessage.mediaObject = imageObject;
        Bitmap imageBitmap = mBuilder.getImageBitmap();
        String imageUri = mBuilder.getImageUri();
        if (imageBitmap != null) {
            imageObject.imageData = SocialUtils.bitmapToBytes(imageBitmap, SocialConfig.WECHAT_IMAGE_LIMIT);
        } else {
            imageObject.imageData = SocialUtils.loadImageBytes(imageUri, SocialConfig.WECHAT_IMAGE_LIMIT);
        }
        mediaMessage.thumbData = SocialUtils.scaleImage(imageObject.imageData, SocialConfig.WECHAT_THUMB_LIMIT);
        return mediaMessage;
    }

    private WXMediaMessage createVideoObject() {
        WXVideoObject videoObject = new WXVideoObject();
        videoObject.videoUrl = mBuilder.getVideoUrl();

        WXMediaMessage mediaMessage = new WXMediaMessage();
        mediaMessage.mediaObject = videoObject;
        mediaMessage.title = mBuilder.getTitle();
        mediaMessage.description = mBuilder.getText();
        mediaMessage.thumbData = createThumbData(SocialConfig.WECHAT_THUMB_LIMIT);

        return mediaMessage;
    }

    private WXMediaMessage createFileObject() {
        WXFileObject fileObject = new WXFileObject();
        fileObject.setFilePath(mBuilder.getFilePath());
        fileObject.setContentLengthLimit(mBuilder.getFileSizeLimit());

        WXMediaMessage mediaMessage = new WXMediaMessage();
        mediaMessage.mediaObject = fileObject;
        mediaMessage.title = mBuilder.getTitle();
        mediaMessage.description = mBuilder.getText();
        mediaMessage.thumbData = createThumbData(SocialConfig.WECHAT_THUMB_LIMIT);

        return mediaMessage;
    }

    /**
     * 分享链接
     *
     * @return
     */
    private WXMediaMessage createWebpageObject() {
        WXWebpageObject webpageObject = new WXWebpageObject();
        webpageObject.webpageUrl = mBuilder.getPageUrl();
        WXMediaMessage mediaMessage = new WXMediaMessage();
        mediaMessage.mediaObject = webpageObject;
        mediaMessage.title = mBuilder.getTitle();
        mediaMessage.description = mBuilder.getText();
        mediaMessage.messageExt = mBuilder.getExText();
        mediaMessage.thumbData = createThumbData(SocialConfig.WECHAT_THUMB_LIMIT);
        return mediaMessage;
    }

    /**
     * 分享小程序页面
     *
     * @return
     */
    private WXMediaMessage createMiniProgramObject() {
        WXMiniProgramObject programObject = new WXMiniProgramObject();
        programObject.webpageUrl = mBuilder.getPageUrl();
        programObject.userName = mBuilder.getMiniProgramUserName();
        programObject.path = mBuilder.getMiniProgramPath();
        programObject.miniprogramType = mBuilder.getMiniProgramType();

        WXMediaMessage msg = new WXMediaMessage(programObject);
        msg.title = mBuilder.getTitle();
        msg.description = mBuilder.getText();
        msg.thumbData = createThumbData(SocialConfig.WECHAT_MINI_PROG_IMAGE_LIMIT);
        return msg;
    }

    private byte[] createThumbData(int limit) {
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
            String thumbUri = mBuilder.getThumbUri();
            if (!TextUtils.isEmpty(thumbUri)) {
                thumbData = SocialUtils.loadImageBytes(thumbUri, limit);
            }
        }
        return thumbData;
    }

    @Override
    protected void finishImpl() {
        mWXLossResultWorkaround.stop();
        if (mWXApi != null) {
            mWXApi.detach();
        }
    }

    @Override
    public void handleResult(int requestCode, int resultCode, Intent data) {
        Logger.d("WeixinShare", "handleResult=" + requestCode + ", result=" + resultCode +
            ",data=" + data);
        try {
            if (mWXLossResultWorkaround != null) {
                mWXLossResultWorkaround.setHaveResult(true);
                mWXLossResultWorkaround.stop();
            }
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
                finishWithError(new SocialException(getPlatform(), ErrConstants.ERR_AUTH_DENIED, baseResp.errCode, baseResp.errStr, baseResp));
                break;
            default:
                finishWithError(new SocialException(getPlatform(), ErrConstants.ERR_OTHER, baseResp.errCode, baseResp.errStr, baseResp));
                break;
        }
    }

    @Override
    public void onCallback() {
        try {
            if (!mWXLossResultWorkaround.haveResult())
                finishWithNoResult();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
