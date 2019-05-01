package com.laughfly.rxsociallib.platform.weibo;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.laughfly.rxsociallib.SocialConstants;
import com.laughfly.rxsociallib.SocialIntentUtils;
import com.laughfly.rxsociallib.SocialUriUtils;
import com.laughfly.rxsociallib.delegate.SocialDelegateActivity;
import com.laughfly.rxsociallib.exception.SocialShareException;
import com.laughfly.rxsociallib.share.ShareAction;
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
public class WeiboShare extends ShareAction implements WbShareCallback {

    private WbShareHandler mWbShareHandler;

    private boolean mShareByIntent;

    @Override
    protected Class<? extends SocialDelegateActivity> getDelegateActivityClass() {
        return WeiboDelegateActivity.class;
    }

    @Override
    protected void init() throws Exception {
        WbSdk.install(mBuilder.getContext(),
            new AuthInfo(mBuilder.getContext(), mBuilder.getAppId(), mBuilder.getRedirectUrl(), mBuilder.getScope()));
    }

    @Override
    protected void execute() throws Exception {
        SocialDelegateActivity delegate = getDelegate();
        mWbShareHandler = new WbShareHandler(delegate);
        mWbShareHandler.registerApp();
        if(WeiboConstants.WEIBO.equalsIgnoreCase(getPlatform())) {
            shareToWeibo(delegate);
        } else {
            shareToStory();
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
    public void handleResult(int requestCode, int resultCode, Intent data) {
        mWbShareHandler.doResultIntent(data, this);
    }

    private void shareToWeibo(SocialDelegateActivity activity) throws Exception {
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

    private void shareToStory() throws Exception {
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

    private void shareVideo() throws SocialShareException {
        WeiboMultiMessage message = createMessage();
        message.textObject = createTextObject();
        message.videoSourceObject = createVideoObject();
        shareBySDK(message);
    }

    private void shareMultiImage(Activity activity) throws SocialShareException {
        ArrayList<Uri> imageUriList = new ArrayList<>();
        List<String> imageList = mBuilder.getImageList();
        for (String image : imageList) {
            imageUriList.add(Uri.parse(transformUri(image, URI_TYPES_ALL, SocialUriUtils.TYPE_FILE_PATH)));
        }
        Intent intent = SocialIntentUtils.createImageListShare(imageUriList, WeiboConstants.WEIBO_PACKAGE, WeiboConstants.WEIBO_SHARE_TARGET_CLASS);
        activity.startActivity(intent);
        mShareByIntent = true;
    }

    private void shareImageStory() throws Exception {
        StoryMessage message = new StoryMessage();
        String imageUri = getImagePath(WeiboConstants.IMAGE_SIZE_LIMIT);
        if(SocialUriUtils.isFilePath(imageUri)) {
            imageUri = SocialUriUtils.toFileUri(imageUri);
        }
        message.setImageUri(Uri.parse(imageUri));
        shareBySDK(message);
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
        imageObject.imagePath = getImagePath(WeiboConstants.IMAGE_SIZE_LIMIT);
        return imageObject;
    }

    private WebpageObject createWebObject() throws SocialShareException {
        WebpageObject webpageObject = new WebpageObject();
        webpageObject.identify = Utility.generateGUID();
        webpageObject.title = mBuilder.getTitle();
        webpageObject.description = mBuilder.getText();
        webpageObject.actionUrl = mBuilder.getWebUrl();
        webpageObject.thumbData = getThumbBytes(WeiboConstants.WEIBO_THUMB_LIMIT);
        webpageObject.defaultText = mBuilder.getText();
        return webpageObject;
    }

    private VideoSourceObject createVideoObject() throws SocialShareException {
        //获取视频
        VideoSourceObject videoSourceObject = new VideoSourceObject();
        String videoUri = transformUri(mBuilder.getVideoUri(), URI_TYPES_LOCAL, SocialUriUtils.TYPE_FILE_URI | SocialUriUtils.TYPE_CONTENT_URI);
        videoSourceObject.videoPath = Uri.parse(videoUri);
        return videoSourceObject;
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
