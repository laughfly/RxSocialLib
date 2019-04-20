package com.laughfly.rxsociallib.platform.weibo;

import android.app.Activity;
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
import com.laughfly.rxsociallib.exception.SocialShareException;
import com.laughfly.rxsociallib.share.AbsSocialShare;
import com.laughfly.rxsociallib.share.ShareFeature;
import com.laughfly.rxsociallib.share.ShareFeatures;
import com.laughfly.rxsociallib.share.ShareType;
import com.laughfly.rxsociallib.share.SocialShareResult;
import com.sina.weibo.sdk.WbSdk;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.StoryMessage;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.VideoSourceObject;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.share.WbShareCallback;
import com.sina.weibo.sdk.share.WbShareHandler;
import com.sina.weibo.sdk.utils.Utility;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 微博分享
 * author:caowy
 * date:2018-05-26
 */
@ShareFeatures({
    @ShareFeature(platform = WeiboConstants.WEIBO, supportFeatures = WeiboConstants.WEIBO_SHARE_SUPPORT),
    @ShareFeature(platform = WeiboConstants.WEIBO_STORY, supportFeatures = WeiboConstants.WEIBO_STORY_SHARE_SUPPORT)
})
public class WeiboShare extends AbsSocialShare<WeiboDelegateActivity> implements WbShareCallback {

    private WbShareHandler mWbShareHandler;

    private boolean mShareByIntent;

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

        DelegateHelper.startActivity(mBuilder.getContext(), WeiboDelegateActivity.class, WeiboShare.this);
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
                    mWbShareHandler = new WbShareHandler(activity);
                    mWbShareHandler.registerApp();
                    if(WeiboConstants.WEIBO.equalsIgnoreCase(getPlatform())) {
                        shareToWeibo(activity);
                    } else {
                        shareToStory(activity);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    finishWithError(e);
                }
            }
        });
    }

    private void shareToWeibo(WeiboDelegateActivity activity) throws Exception {
        int shareType = getShareType();
        switch (shareType) {
            case ShareType.SHARE_TEXT:
                shareText();
                break;
            case ShareType.SHARE_WEB:
                shareWeb();
                break;
            case ShareType.SHARE_IMAGE:
                shareImage();
                break;
            case ShareType.SHARE_AUDIO:
                shareAudio();
                break;
            case ShareType.SHARE_LOCAL_VIDEO:
                shareVideo();
                break;
            case ShareType.SHARE_MULTI_IMAGE:
                shareMultiImage(activity);
                break;
            case ShareType.SHARE_FILE:
            case ShareType.SHARE_MINI_PROGRAM:
            case ShareType.SHARE_MULTI_FILE:
            case ShareType.SHARE_APP:
            case ShareType.SHARE_NONE:
            default:
                throw new SocialShareException(getPlatform(), SocialConstants.ERR_SHARETYPE_NOT_SUPPORT);
        }
    }

    private void shareToStory(Activity activity) throws Exception {
        int shareType = getShareType();
        switch (shareType) {
            case ShareType.SHARE_IMAGE:
                shareImageStory();
                break;
            case ShareType.SHARE_LOCAL_VIDEO:
                shareVideoStory();
                break;
            default:
                throw new SocialShareException(getPlatform(), SocialConstants.ERR_SHARETYPE_NOT_SUPPORT);
        }
    }

    private void shareText() {
        WeiboMultiMessage message = createMessage();
        message.textObject = createTextObject();
        shareBySDK(message);
    }

    private void shareImage() throws Exception {
        WeiboMultiMessage message = createMessage();
        if(mBuilder.hasText()) {
            message.textObject = createTextObject();
        }
        message.imageObject = createImageObj();
        shareBySDK(message);
    }

    private void shareWeb() throws SocialShareException {
        WeiboMultiMessage message = createMessage();
        message.mediaObject = createWebObject();
        shareBySDK(message);
    }

    private void shareAudio() throws SocialShareException {
        WeiboMultiMessage message = createMessage();
        mBuilder.setWebUrl(mBuilder.getAudioUri());
        message.mediaObject = createWebObject();
        shareBySDK(message);
    }

    private void shareVideo() {
        WeiboMultiMessage message = createMessage();
        message.textObject = createTextObject();
        message.videoSourceObject = createVideoObject();
        shareBySDK(message);
    }

    private void shareMultiImage(Activity activity) {
        ArrayList<Uri> imageUriList = new ArrayList<>();
        List<String> imageList = mBuilder.getImageList();
        for (String image : imageList) {
            imageUriList.add(Uri.parse(image));
        }
        Intent intent = SocialIntentUtils.createImageListShare(imageUriList, WeiboConstants.WEIBO_PACKAGE, WeiboConstants.WEIBO_SHARE_TARGET_CLASS);
        activity.startActivity(intent);
        mShareByIntent = true;
    }

    private void shareImageStory() throws Exception {
        StoryMessage storyMessage = new StoryMessage();
        String imageUri = downloadImageIfNeed(mBuilder.getImageUri());
        if(SocialUriUtils.isFilePath(imageUri)) {
            imageUri = SocialUriUtils.toFileUri(imageUri);
        }
        storyMessage.setImageUri(Uri.parse(imageUri));
        shareBySDK(storyMessage);
    }

    private void shareVideoStory() {
        StoryMessage storyMessage = new StoryMessage();
        String videoUri = mBuilder.getVideoUri();
        if(SocialUriUtils.isFilePath(videoUri)) {
            videoUri = SocialUriUtils.toFileUri(videoUri);
        }
        storyMessage.setVideoUri(Uri.parse(videoUri));
        shareBySDK(storyMessage);
    }

    private WeiboMultiMessage createMessage() {
        WeiboMultiMessage message = new WeiboMultiMessage();
        return message;
    }

    private void shareBySDK(WeiboMultiMessage weiboMessage) {
        mWbShareHandler.shareMessage(weiboMessage, false);
    }

    private void shareBySDK(StoryMessage storyMessage) {
        mWbShareHandler.shareToStory(storyMessage);
    }

    private TextObject createTextObject() {
        TextObject textObject = new TextObject();
        textObject.text = mBuilder.getText();
        return textObject;
    }

    private ImageObject createImageObj() throws Exception {
        ImageObject imageObject = new ImageObject();
        Bitmap imageBitmap = mBuilder.getImageBitmap();
        String imageUri = mBuilder.getImageUri();
        if (imageBitmap != null) {
            imageObject.setImageObject(imageBitmap);
            return imageObject;
        } else if (imageUri != null) {
            imageObject.imagePath = downloadImageIfNeed(imageUri);
            return imageObject;
        } else {
            return null;
        }
    }

    private WebpageObject createWebObject() throws SocialShareException {
        WebpageObject webpageObject = new WebpageObject();
        webpageObject.identify = Utility.generateGUID();
        webpageObject.title = mBuilder.getTitle();
        webpageObject.description = mBuilder.getText();
        webpageObject.actionUrl = mBuilder.getWebUrl();
        webpageObject.thumbData = createThumbData(WeiboConstants.WEIBO_THUMB_LIMIT);
        webpageObject.defaultText = mBuilder.getText();
        return webpageObject;
    }

    private VideoSourceObject createVideoObject(){
        //获取视频
        VideoSourceObject videoSourceObject = new VideoSourceObject();
        String videoUri = mBuilder.getVideoUri();
        Uri uri;
        if(SocialUriUtils.isFilePath(videoUri)) {//文件路径前面添加file://
            uri = Uri.fromFile(new File(videoUri));
        } else {
            uri = Uri.parse(videoUri);
        }
        videoSourceObject.videoPath = uri;
        return videoSourceObject;
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
    public void handleNoResult() {
        if(mShareByIntent) {
            finishWithSuccess(new SocialShareResult(getPlatform()));
        } else {
            super.handleNoResult();
        }
    }

    @Override
    public void handleResult(int requestCode, int resultCode, Intent data) {
        super.handleResult(requestCode, resultCode, data);
//        if (requestCode != 0 && requestCode != -1) {
//            SocialThreads.postOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    finishWithCancel();
//                }
//            }, WeiboShare.this, 100);
//            return;
//        }
//        SocialThreads.removeUiRunnable(WeiboShare.this);
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
