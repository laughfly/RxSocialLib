package com.laughfly.rxsociallib.share;

import android.graphics.Bitmap;

import com.laughfly.rxsociallib.SocialConstants;
import com.laughfly.rxsociallib.SocialModel;
import com.laughfly.rxsociallib.internal.SocialParams;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by caowy on 2019/5/23.
 * email:cwy.fly2@gmail.com
 */
public class ShareParams extends SocialParams {

    public String getShareAppName() {
        return getString("shareAppName");
    }

    void setShareAppName(String appName) {
        putString("shareAppName", appName);
    }

    public String getTitle() {
        return getString("title");
    }

    void setTitle(String title) {
        putString("title", title);
    }

    public String getText() {
        return getString("text");
    }

    void setText(String text) {
        putString("text", text);
    }

    public String getExText() {
        return getString("exText");
    }

    void setExText(String exText) {
        putString("exText", exText);
    }

    public String getWebUrl() {
        return getString("webUrl");
    }

    void setWebUrl(String targetUrl) {
        putString("webUrl", targetUrl);
    }

    public boolean hasWebUrl() {
        return containsKey("webUrl");
    }

    void setThumbUri(String uri) {
        putString("thumbUri", uri);
    }

    public String getThumbUri() {
        return getString("thumbUri");
    }

    void setThumbBitmap(Bitmap bitmap) {
        putParcelable("thumbBitmap", bitmap);
    }

    public Bitmap getThumbBitmap() {
        return getParcelable("thumbBitmap");
    }

    public int getThumbResId() {
        return getInt("thumbResId", 0);
    }

    void setThumbResId(int imageResId) {
        putInt("thumbResId", imageResId);
    }


    public int getThumbSize() {
        return getInt("thumbSize", 72);
    }

    void setThumbSize(int thumbSize) {
        putInt("thumbSize", thumbSize);
    }

    public boolean isScaleThumb() {
        return getBoolean("isScaleThumb", false);
    }

    void setScaleThumb(boolean scaleThumb) {
        putBoolean("isScaleThumb", scaleThumb);
    }

    public String getImageUri() {
        return getString("imageUri");
    }

    void setImageUri(String uri) {
        putString("imageUri", uri);
    }

    public Bitmap getImageBitmap() {
        return getParcelable("imageBitmap");
    }

    void setImageBitmap(Bitmap imageBitmap) {
        putParcelable("imageBitmap", imageBitmap);
    }

    public int getImageResId() {
        return getInt("imageRes", 0);
    }

    void setImageResId(int imageResId) {
        putInt("imageRes", imageResId);
    }

    public int getImageSize() {
        return getInt("imageSize", 720);
    }

    void setImageSize(int thumbSize) {
        putInt("imageSize", thumbSize);
    }

    public boolean hasText() {
        return containsKey("title") || containsKey("text") || containsKey("exText");
    }

    public boolean hasImage() {
        return containsKey("imageBitmap") || containsKey("imageUri") || containsKey("imageRes");
    }

    void setImageList(ArrayList<String> imageList) {
        putStringArrayList("imageList", imageList);
    }

    public ArrayList<String> getImageList() {
        return getStringArrayList("imageList");
    }

    public boolean hasImageList() {
        List<String> imageList = getImageList();
        return null != imageList && imageList.size() > 0;
    }

    void setMiniProgramUserName(String userName) {
        putString("miniProgramUserName", userName);
    }

    public String getMiniProgramUserName() {
        return getString("miniProgramUserName");
    }

    void setMiniProgramType(@SocialConstants.MiniProgramType int type) {
        putInt("miniProgramType", type);
    }

    public int getMiniProgramType() {
        return getInt("miniProgramType", 0);
    }

    void setMiniProgramPath(String path) {
        putString("miniProgramPath", path);
    }

    public String getMiniProgramPath() {
        return getString("miniProgramPath");
    }

    public boolean hasMiniProgram() {
        return containsKey("miniProgramPath");
    }

    void setAudioUri(String uri) {
        putString("audioUri", uri);
    }

    public String getAudioUri() {
        return getString("audioUri");
    }

    public boolean hasAudio() {
        return containsKey("audioUri");
    }

    void setVideoUri(String uri) {
        putString("videoUri", uri);
    }

    public String getVideoUri() {
        return getString("videoUri");
    }

    public boolean hasVideo() {
        return containsKey("videoUri");
    }

    void setFileUri(String path) {
        putString("fileUri", path);
    }

    public String getFileUri() {
        return getString("fileUri");
    }

    public boolean hasFilePath() {
        return containsKey("fileUri");
    }

    void setFileSizeLimit(int sizeLimit) {
        putInt("fileSizeLimit", sizeLimit);
    }

    void setFileList(ArrayList<String> fileList) {
        putStringArrayList("fileList", fileList);
    }

    public List<String> getFileList() {
        return getStringArrayList("fileList");
    }

    public boolean hasFileList() {
        return containsKey("fileList");
    }

    public int getFileSizeLimit() {
        return getInt("fileSizeLimit", 104857600);
    }

    void setAppInfo(String arkInfo) {
        putString("appInfo", arkInfo);
    }

    public String getAppInfo() {
        return getString("appInfo");
    }

    public boolean hasAppInfo() {
        return containsKey("appInfo");
    }

    void setNoResultAsSuccess(boolean noResultAsSuccess) {
        putBoolean("noResultAsSuccess", noResultAsSuccess);
    }

    public boolean getNoResultAsSuccess() {
        return getBoolean("noResultAsSuccess", SocialModel.getTreatNoResultAsSuccess());
    }
}
