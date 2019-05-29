package com.laughfly.rxsociallib.share;

import android.content.Context;
import android.graphics.Bitmap;

import com.laughfly.rxsociallib.PlatformConfig;
import com.laughfly.rxsociallib.SocialConstants;
import com.laughfly.rxsociallib.internal.SocialBuilder;

import java.util.ArrayList;

/**
 * 分享参数Builder
 * author:caowy
 * date:2018-05-26
 */
public class ShareBuilder extends SocialBuilder<ShareParams> {

    public ShareBuilder(Context context, String platform, PlatformConfig platformConfig) {
        super(context, platform, platformConfig);
    }

    @Override
    protected ShareParams createParams() {
        return new ShareParams();
    }

    public ShareExecutor build() {
        return new ShareExecutor(mParams);
    }

    public ShareBuilder setShareAppName(String appName) {
        mParams.setShareAppName(appName);
        return this;
    }

    public ShareBuilder setTitle(String title) {
        mParams.setTitle(title);
        return this;
    }

    public ShareBuilder setText(String text) {
        mParams.setText(text);
        return this;
    }

    public ShareBuilder setExText(String exText) {
        mParams.setExText(exText);
        return this;
    }

    public ShareBuilder setWebUrl(String webUrl) {
        mParams.setWebUrl(webUrl);
        return this;
    }

    public ShareBuilder setThumbUri(String uri) {
        mParams.setThumbUri(uri);
        return this;
    }

    public ShareBuilder setThumbBitmap(Bitmap bitmap) {
        mParams.setThumbBitmap(bitmap);
        return this;
    }

    public ShareBuilder setThumbResId(int imageResId) {
        mParams.setThumbResId(imageResId);
        return this;
    }

    public ShareBuilder setThumbSize(int thumbSize) {
        mParams.setThumbSize(thumbSize);
        return this;
    }

    public ShareBuilder setScaleThumb(boolean scaleThumb) {
        mParams.setScaleThumb(scaleThumb);
        return this;
    }

    public ShareBuilder setImageUri(String imageUri) {
        mParams.setImageUri(imageUri);
        return this;
    }

    public ShareBuilder setImageBitmap(Bitmap imageBitmap) {
        mParams.setImageBitmap(imageBitmap);
        return this;
    }

    public ShareBuilder setImageResId(int imageResId) {
        mParams.setImageResId(imageResId);
        return this;
    }

    public ShareBuilder setImageSize(int imageSize) {
        mParams.setImageSize(imageSize);
        return this;
    }

    public ShareBuilder setImageList(ArrayList<String> imageList) {
        mParams.setImageList(imageList);
        return this;
    }

    public ShareBuilder setMiniProgramUserName(String userName) {
        mParams.setMiniProgramUserName(userName);
        return this;
    }

    public ShareBuilder setMiniProgramType(@SocialConstants.MiniProgramType int type) {
        mParams.setMiniProgramType(type);
        return this;
    }

    public ShareBuilder setMiniProgramPath(String path) {
        mParams.setMiniProgramPath(path);
        return this;
    }

    public ShareBuilder setAudioUri(String uri) {
        mParams.setAudioUri(uri);
        return this;
    }

    public ShareBuilder setVideoUri(String uri) {
        mParams.setVideoUri(uri);
        return this;
    }

    public ShareBuilder setFileUri(String path) {
        mParams.setFileUri(path);
        return this;
    }

    public ShareBuilder setFileSizeLimit(int sizeLimit) {
        mParams.setFileSizeLimit(sizeLimit);
        return this;
    }

    public ShareBuilder setFileList(ArrayList<String> fileList) {
        mParams.setFileList(fileList);
        return this;
    }

    public ShareBuilder setAppInfo(String arkInfo) {
        mParams.setAppInfo(arkInfo);
        return this;
    }

    public ShareBuilder setNoResultAsSuccess(boolean noResultAsSuccess) {
        mParams.setNoResultAsSuccess(noResultAsSuccess);
        return this;
    }
}
