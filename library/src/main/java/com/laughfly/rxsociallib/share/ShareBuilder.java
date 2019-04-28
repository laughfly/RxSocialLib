package com.laughfly.rxsociallib.share;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;

import com.laughfly.rxsociallib.PlatformConfig;
import com.laughfly.rxsociallib.SocialActionFactory;
import com.laughfly.rxsociallib.SocialConstants;
import com.laughfly.rxsociallib.SocialModel;
import com.laughfly.rxsociallib.internal.SocialBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 分享参数Builder
 * author:caowy
 * date:2018-05-26
 */
public class ShareBuilder extends SocialBuilder<AbsSocialShare, SocialShareResult> {

    public ShareBuilder(Context context, String platform, PlatformConfig platformConfig) {
        super(context, platform, platformConfig);
    }

    @Override
    protected AbsSocialShare build() {
        return SocialActionFactory.createShareAction(getPlatform(), this);
    }

    protected boolean checkArgs() {
        return getContext() != null && !TextUtils.isEmpty(getAppId());
    }

    public ShareBuilder setAll(Map<String, Object> data) {
        putAll(data);
        return this;
    }

    public <T> ShareBuilder putExtra(String key, T entity) {
        put("extra-" + key, entity);
        return this;
    }

    public <T> T getExtra(String key) {
        return (T) get("extra-" + key);
    }

    public String getShareAppName() {
        return get("shareAppName");
    }

    public ShareBuilder setShareAppName(String appName) {
        put("shareAppName", appName);
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

    public String getWebUrl() {
        return get("webUrl");
    }

    public ShareBuilder setWebUrl(String targetUrl) {
        put("webUrl", targetUrl);
        return this;
    }

    public boolean hasWebUrl() {
        return has("webUrl");
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
        return has("title") || has("text") || has("exText");
    }

    public boolean hasImage() {
        return has("imageBitmap") || has("imageUri") || has("imageRes");
    }

    public ShareBuilder setImageList(String... imageList) {
        return setImageList(Arrays.asList(imageList));
    }

    public ShareBuilder setImageList(List<String> imageList) {
        put("imageList", imageList);
        return this;
    }

    public List<String> getImageList() {
        return get("imageList");
    }

    public boolean hasImageList() {
        List<String> imageList = getImageList();
        return null != imageList && imageList.size() > 0;
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

    public boolean hasMiniProgram() {
        return has("miniProgramPath");
    }

    public ShareBuilder setAudioUri(String uri) {
        put("audioUri", uri);
        return this;
    }

    public String getAudioUri() {
        return get("audioUri");
    }

    public boolean hasAudio() {
        return has("audioUri");
    }

    public ShareBuilder setVideoUri(String uri) {
        put("videoUri", uri);
        return this;
    }

    public String getVideoUri() {
        return get("videoUri");
    }

    public boolean hasVideo() {
        return has("videoUri");
    }

    public ShareBuilder setFileUri(String path) {
        put("fileUri", path);
        return this;
    }

    public String getFileUri() {
        return get("fileUri");
    }

    public boolean hasFilePath() {
        return has("fileUri");
    }

    public ShareBuilder setFileSizeLimit(int sizeLimit) {
        put("fileSizeLimit", sizeLimit);
        return this;
    }

    public ShareBuilder setFileList(List<String> fileList) {
        put("fileList", fileList);
        return this;
    }

    public List<String> getFileList() {
        return get("fileList");
    }

    public boolean hasFileList() {
        return has("fileList");
    }

    public int getFileSizeLimit() {
        return get("fileSizeLimit", 104857600);
    }

    public ShareBuilder setAppInfo(String arkInfo) {
        put("appInfo", arkInfo);
        return this;
    }

    public String getAppInfo() {
        return get("appInfo");
    }

    public boolean hasAppInfo() {
        return has("appInfo");
    }

    public ShareBuilder setNoResultAsSuccess(boolean noResultAsSuccess) {
        put("noResultAsSuccess", noResultAsSuccess);
        return this;
    }

    public boolean getNoResultAsSuccess() {
        return get("noResultAsSuccess", SocialModel.getNoResultAsSuccess());
    }
}
