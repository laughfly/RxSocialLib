package com.laughfly.rxsociallib.share;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import com.laughfly.rxsociallib.SocialCallback;
import com.laughfly.rxsociallib.SocialConstants;
import com.laughfly.rxsociallib.SocialModel;
import com.laughfly.rxsociallib.SocialUriUtils;
import com.laughfly.rxsociallib.SocialUtils;
import com.laughfly.rxsociallib.delegate.SocialDelegateActivity;
import com.laughfly.rxsociallib.exception.SocialShareException;
import com.laughfly.rxsociallib.internal.SocialAction;

import java.io.File;

/**
 * 分享的基础类
 * author:caowy
 * date:2018-04-20
 *
 * @param <Delegate>
 */
public abstract class AbsSocialShare<Delegate extends SocialDelegateActivity> extends SocialAction<ShareBuilder, Delegate, SocialShareResult> {

    protected static @SocialUriUtils.UriType int URI_TYPES_ALL = SocialUriUtils.TYPE_HTTP | SocialUriUtils.TYPE_FILE_URI | SocialUriUtils.TYPE_FILE_PATH | SocialUriUtils.TYPE_CONTENT_URI;

    protected static @SocialUriUtils.UriType int URI_TYPES_LOCAL = SocialUriUtils.TYPE_FILE_URI | SocialUriUtils.TYPE_FILE_PATH | SocialUriUtils.TYPE_CONTENT_URI;

    protected static @SocialUriUtils.UriType int URI_TYPES_NETWORK = SocialUriUtils.TYPE_HTTP;

    public AbsSocialShare() {
        super();
    }

    @Override
    public AbsSocialShare setCallback(SocialCallback<SocialShareResult> callback) {
        super.setCallback(callback);
        return this;
    }

    @Override
    public void handleNoResult() {
        if(mBuilder.getNoResultAsSuccess()) {
            finishWithSuccess(new SocialShareResult(getPlatform()));
        } else {
            super.handleNoResult();
        }
    }

    @Override
    protected void finishWithCancel() {
        finishWithError(new SocialShareException(getPlatform(), SocialConstants.ERR_USER_CANCEL));
    }

    @Override
    protected void finishWithNoResult() {
        finishWithError(new SocialShareException(getPlatform(), SocialConstants.ERR_NO_RESULT));
    }

    @Override
    protected void finishWithError(Exception e) {
        finishWithError(new SocialShareException(getPlatform(), e));
    }

    @Override
    protected void onStart() throws Exception {
        super.onStart();
        @ShareType.Def int type = getShareType();
        if(!isShareTypeSupport(type)) {
            throw new SocialShareException(getPlatform(), SocialConstants.ERR_SHARETYPE_NOT_SUPPORT);
        }
    }

    protected String downloadFileIfNeed(String imageUri) throws SocialShareException {
        try {
            if (SocialUriUtils.isHttpUrl(imageUri)) {
                File downloadFile = SocialModel.getFileDownloader().download(imageUri);
                imageUri = downloadFile.getAbsolutePath();
            }
            return imageUri;
        } catch (Exception e) {
            throw new SocialShareException(getPlatform(), SocialConstants.ERR_DOWNLOAD_FAILED, imageUri);
        }
    }

    /**
     * @param uri the original uri
     * @param acceptUriTypes library accept types
     * @param targetUriTypes platform accept types
     * @return the transformed uri
     * @throws SocialShareException
     */
    protected String transformUri(String uri, @SocialUriUtils.UriType int acceptUriTypes, @SocialUriUtils.UriType int targetUriTypes) throws SocialShareException {
        int uriType = SocialUriUtils.getUriType(uri);

        if(!SocialUriUtils.containType(acceptUriTypes, uriType)) {
            throw new SocialShareException(getPlatform(), SocialConstants.ERR_URI_NOT_SUPPORT);
        }
        String transformUri = uri;

        int targetType = SocialUriUtils.getTargetType(targetUriTypes, uriType);
        if(targetType != uriType && SocialUriUtils.TYPE_NULL != targetType) {
            if(SocialUriUtils.TYPE_HTTP == uriType) {//download to file path
                uri = downloadFileIfNeed(uri);
            } else if(SocialUriUtils.TYPE_CONTENT_URI == uriType) {//to file path
                uri = SocialUriUtils.toFilePath(mBuilder.getContext(), uri, SocialModel.getDownloadDirectory());
            } else if(SocialUriUtils.TYPE_FILE_URI == uriType) {//to file path
                uri = SocialUriUtils.toFilePath(uri);
            }
            //transform file path
            switch (targetType) {
                case SocialUriUtils.TYPE_CONTENT_URI:
                    transformUri = SocialUriUtils.getContentUri(mBuilder.getContext(), new File(uri)).toString();
                    break;
                case SocialUriUtils.TYPE_FILE_PATH:
                    transformUri = uri;
                    break;
                case SocialUriUtils.TYPE_FILE_URI:
                    transformUri = SocialUriUtils.toFileUri(uri);
                    break;
            }
        }
        return transformUri;
    }

    protected String getImagePath(int sizeLimit) throws SocialShareException {
        Bitmap imageBitmap = mBuilder.getImageBitmap();
        if(imageBitmap != null) {
            File thumbFile = new File(SocialModel.getDownloadDirectory(), imageBitmap.getGenerationId() + (imageBitmap.hasAlpha() ? ".png" : ".jpg"));
            boolean saved = SocialUtils.saveBitmapToFile(imageBitmap, thumbFile, sizeLimit);
            if(saved) {
                return thumbFile.getAbsolutePath();
            }
        }
        int imageResId = mBuilder.getImageResId();
        if(imageResId != 0) {
            Bitmap bitmap = BitmapFactory.decodeResource(mBuilder.getContext().getResources(), imageResId);
            File thumbFile = new File(SocialModel.getDownloadDirectory(), bitmap.getGenerationId() + (bitmap.hasAlpha() ? ".png" : ".jpg"));
            boolean saved = SocialUtils.saveBitmapToFile(bitmap, thumbFile, sizeLimit);
            if(saved) {
                return thumbFile.getAbsolutePath();
            }
        }
        String imageUri = mBuilder.getImageUri();
        if(!TextUtils.isEmpty(imageUri)) {
            imageUri = transformUri(imageUri, URI_TYPES_ALL, SocialUriUtils.TYPE_FILE_PATH);
            return imageUri;
        }
        return null;
    }

    protected String getThumbPath(int sizeLimit) throws SocialShareException {
        Bitmap thumbBitmap = mBuilder.getThumbBitmap();
        if(thumbBitmap != null) {
            File thumbFile = new File(SocialModel.getDownloadDirectory(), thumbBitmap.getGenerationId() + (thumbBitmap.hasAlpha() ? ".png" : ".jpg"));
            boolean saved = SocialUtils.saveBitmapToFile(thumbBitmap, thumbFile, sizeLimit);
            if(saved) {
                return thumbFile.getAbsolutePath();
            }
        }
        int thumbResId = mBuilder.getThumbResId();
        if(thumbResId != 0) {
            Bitmap bitmap = BitmapFactory.decodeResource(mBuilder.getContext().getResources(), thumbResId);
            File thumbFile = new File(SocialModel.getDownloadDirectory(), bitmap.getGenerationId() + (bitmap.hasAlpha() ? ".png" : ".jpg"));
            boolean saved = SocialUtils.saveBitmapToFile(bitmap, thumbFile, sizeLimit);
            if(saved) {
                return thumbFile.getAbsolutePath();
            }
        }
        String thumbUri = mBuilder.getThumbUri();
        if(!TextUtils.isEmpty(thumbUri)) {
            thumbUri = transformUri(thumbUri, URI_TYPES_ALL, SocialUriUtils.TYPE_FILE_PATH);
            return thumbUri;
        }
        return null;
    }

    protected byte[] getThumbBytes(int sizeLimit) throws SocialShareException {
        Bitmap thumbBitmap = mBuilder.getThumbBitmap();
        if(thumbBitmap != null) {
            return SocialUtils.bitmapToBytes(thumbBitmap, sizeLimit, false);
        }
        String thumbUri = mBuilder.getThumbUri();
        if(!TextUtils.isEmpty(thumbUri)) {
            thumbUri = transformUri(thumbUri, URI_TYPES_ALL, SocialUriUtils.TYPE_FILE_PATH);
            return SocialUtils.loadImageBytes(thumbUri, sizeLimit);
        }
        int thumbResId = mBuilder.getThumbResId();
        if(thumbResId != 0) {
            Bitmap bitmap = BitmapFactory.decodeResource(mBuilder.getContext().getResources(), thumbResId);
            return SocialUtils.bitmapToBytes(bitmap, sizeLimit, true);
        }
        return null;
    }

    protected boolean isShareTypeSupport(@ShareType.Def int shareType) {
        return (getSupportShareType() & shareType) != ShareType.SHARE_NONE;
    }

    protected @ShareType.Def int getSupportShareType() {
        ShareFeatures shareFeatures = getClass().getAnnotation(ShareFeatures.class);
        ShareFeature shareFeature = null;
        if(shareFeatures != null) {
            for (ShareFeature feature : shareFeatures.value()) {
                if(feature.platform().equalsIgnoreCase(getPlatform())) {
                    shareFeature = feature;
                    break;
                }
            }
        }
        return shareFeature != null ? shareFeature.supportFeatures() : ShareType.SHARE_NONE;
    }

    protected @ShareType.Def int getShareType() {
        ShareBuilder builder = getBuilder();
        if(builder.hasMiniProgram()) {
            return ShareType.SHARE_MINI_PROGRAM;
        }
        if(builder.hasVideo()) {
            if(SocialUriUtils.isHttpUrl(builder.getVideoUri())) {
                return ShareType.SHARE_NETWORK_VIDEO;
            } else {
                return ShareType.SHARE_LOCAL_VIDEO;
            }
        }

        if(builder.hasAudio()) {
            return ShareType.SHARE_AUDIO;
        }

        if (builder.hasImage()) {
            return ShareType.SHARE_IMAGE;
        }

        if (builder.hasImageList()) {
            return ShareType.SHARE_MULTI_IMAGE;
        }

        if (builder.hasAppInfo()) {
            return ShareType.SHARE_APP;
        }

        if(builder.hasFileList()) {
            return ShareType.SHARE_MULTI_FILE;
        }

        if (builder.hasFilePath()) {
            return ShareType.SHARE_FILE;
        }

        if(builder.hasWebUrl()) {
            return ShareType.SHARE_WEB;
        }

        return ShareType.SHARE_TEXT;
    }
}
