package com.laughfly.rxsociallib.share;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;

import com.laughfly.rxsociallib.Platform;
import com.laughfly.rxsociallib.PlatformConfig;
import com.laughfly.rxsociallib.SocialCallback;
import com.laughfly.rxsociallib.SocialConstants;
import com.laughfly.rxsociallib.SocialUtils;
import com.laughfly.rxsociallib.internal.SocialBuilder;
import com.laughfly.rxsociallib.platform.qq.QQShare;
import com.laughfly.rxsociallib.platform.weibo.WeiboShare;
import com.laughfly.rxsociallib.platform.weixin.WeixinShare;

import rx.Observable;

/**
 * 分享参数Builder
 * author:caowy
 * date:2018-05-26
 */
public class ShareBuilder extends SocialBuilder<AbsSocialShare, SocialShareResult> {

    public ShareBuilder(Context context, Platform platform, PlatformConfig platformConfig) {
        super(context, platform, platformConfig);
    }

    @Override
    public Observable<SocialShareResult> toObservable() {
        return build().toObservable();
    }

    @Override
    public void start(SocialCallback<SocialShareResult> callback) {
        AbsSocialShare share = build();
        share.setCallback(callback);
        share.start();
    }

    @Override
    protected AbsSocialShare build() {
        AbsSocialShare share = null;
        switch (getPlatform()) {
            case QQ:
            case QQ_ZONE:
                share = new QQShare(this);
                break;
            case WEIXIN_MOMENTS:
            case WEIXIN:
                share = new WeixinShare(this);
                break;
            case WEIBO:
                share = new WeiboShare(this);
                break;
        }
        return share;
    }

    protected boolean checkArgs() {
        if (getContext() == null) return false;
        return !SocialUtils.isEmpty(getAppId());
    }


    public <T> ShareBuilder putExtra(String key, T entity) {
        put("extra-" + key, entity);
        return this;
    }

    public <T> T getExtra(String key) {
        return (T) get("extra-" + key);
    }

    public String getAppName() {
        return get("appName");
    }

    public ShareBuilder setAppName(String appName) {
        put("appName", appName);
        return this;
    }

    public String getTitle() {
        return get("title");
    }

    public ShareBuilder setTitle(String title) {
        put("title", title);
        return this;
    }

    public String getText() {
        return get("text");
    }

    public ShareBuilder setText(String text) {
        put("text", text);
        return this;
    }

    public String getExText() {
        return get("exText");
    }

    public ShareBuilder setExText(String exText) {
        put("exText", exText);
        return this;
    }

    public String getPageUrl() {
        return get("pageUrl");
    }

    public ShareBuilder setPageUrl(String targetUrl) {
        put("pageUrl", targetUrl);
        return this;
    }

    public ShareBuilder setThumbUri(String uri) {
        put("thumbUri", uri);
        return this;
    }

    public String getThumbUri() {
        return get("thumbUri");
    }

    public ShareBuilder setThumbBitmap(Bitmap bitmap) {
        put("thumbBitmap", bitmap);
        return this;
    }

    public Bitmap getThumbBitmap() {
        return get("thumbBitmap");
    }

    public int getThumbResId() {
        return get("thumbResId", 0);
    }

    public ShareBuilder setThumbResId(int imageResId) {
        put("thumbResId", imageResId);
        return this;
    }


    public int getThumbSize() {
        return get("thumbSize", 72);
    }

    public ShareBuilder setThumbSize(int thumbSize) {
        put("thumbSize", thumbSize);
        return this;
    }

    public boolean isScaleThumb() {
        return get("isScaleThumb", false);
    }

    public ShareBuilder setScaleThumb(boolean scaleThumb) {
        put("isScaleThumb", scaleThumb);
        return this;
    }

    public String getImageUri() {
        return get("imageUri");
    }

    public ShareBuilder setImageUri(String uri) {
        put("imageUri", uri);
        return this;
    }

    public Bitmap getImageBitmap() {
        return get("imageBitmap");
    }

    public ShareBuilder setImageBitmap(Bitmap imageBitmap) {
        put("imageBitmap", imageBitmap);
        return this;
    }

    public int getImageResId() {
        return get("imageRes", 0);
    }

    public ShareBuilder setImageResId(int imageResId) {
        put("imageRes", imageResId);
        return this;
    }

    public int getImageSize() {
        return get("imageSize", 720);
    }

    public ShareBuilder setImageSize(int thumbSize) {
        put("imageSize", thumbSize);
        return this;
    }

    public boolean hasText() {
        return !TextUtils.isEmpty(getTitle()) || !TextUtils.isEmpty(getText()) || !TextUtils.isEmpty(getExText());
    }

    public boolean hasImage() {
        return getImageBitmap() != null || !TextUtils.isEmpty(getImageUri()) || getImageResId() != 0;
    }

    public ShareBuilder setMiniProgramUserName(String userName) {
        put("miniProgramUserName", userName);
        return this;
    }

    public String getMiniProgramUserName() {
        return get("miniProgramUserName");
    }

    public ShareBuilder setMiniProgramType(@SocialConstants.MiniProgramType int type) {
        put("miniProgramType", type);
        return this;
    }

    public int getMiniProgramType() {
        return get("miniProgramType", 0);
    }


    public ShareBuilder setMiniProgramPath(String path) {
        put("miniProgramPath", path);
        return this;
    }

    public String getMiniProgramPath() {
        return get("miniProgramPath");
    }

    public ShareBuilder setAudioUri(String uri) {
        put("audioUri", uri);
        return this;
    }

    public String getAudioUri() {
        return get("audioUri");
    }

    public ShareBuilder setVideoUrl(String uri) {
        put("videoUrl", uri);
        return this;
    }

    public String getVideoUrl() {
        return get("videoUrl");
    }

    public ShareBuilder setFilePath(String path) {
        put("filePath", path);
        return this;
    }

    public String getFilePath() {
        return get("filePath");
    }

    public ShareBuilder setFileSizeLimit(int sizeLimit) {
        put("fileSizeLimit", sizeLimit);
        return this;
    }

    public int getFileSizeLimit() {
        return get("fileSizeLimit", 104857600);
    }

    public ShareBuilder setArkInfo(String arkInfo) {
        put("arkInfo", arkInfo);
        return this;
    }

    public String getArkInfo() {
        return get("arkInfo");
    }
}
