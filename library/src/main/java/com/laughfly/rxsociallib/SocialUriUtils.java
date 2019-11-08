package com.laughfly.rxsociallib;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;

import androidx.annotation.IntDef;
import androidx.core.content.FileProvider;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by caowy on 2019/4/16.
 * email:cwy.fly2@gmail.com
 */

public class SocialUriUtils {
    public static final int TYPE_NULL = 0;

    public static final int TYPE_FILE_URI = 1;

    public static final int TYPE_FILE_PATH = 1 << 1;

    public static final int TYPE_CONTENT_URI = 1 << 2;

    public static final int TYPE_HTTP = 1 << 3;

    public static final int TYPE_OTHER = 1 << 4;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef(flag = true, value = {TYPE_NULL, TYPE_FILE_PATH, TYPE_FILE_URI, TYPE_CONTENT_URI, TYPE_HTTP, TYPE_OTHER})
    public @interface UriType {}

    public static boolean isContentUri(String uri) {
        return TYPE_CONTENT_URI == getUriType(uri);
    }

    public static boolean isFileUri(String uri) {
        return TYPE_FILE_URI == getUriType(uri);
    }

    public static boolean isFilePath(String uri) {
        return TYPE_FILE_PATH == getUriType(uri);
    }

    public static boolean isHttpUrl(String uri) {
        return TYPE_HTTP == getUriType(uri);
    }

    public static String toFileUri(String filePath) {
        return filePath != null && TYPE_FILE_PATH == getUriType(filePath) ? Uri.fromFile(new File(filePath)).toString() : null;
    }

    public static String toFilePath(String fileUri) {
        return fileUri != null && TYPE_FILE_URI == getUriType(fileUri) ? Uri.parse(fileUri).getPath() : null;
    }

    public static String toFilePath(Context context, String contentUri) {
        return toFilePath(context, contentUri, null);
    }

    public static String toFilePath(Context context, String contentUri, File storeDirectory) {
        if(contentUri == null) return null;
        int uriType = getUriType(contentUri);
        switch (uriType) {
            case TYPE_CONTENT_URI:
                return resolveContentUriPath(context, contentUri, storeDirectory);
            case TYPE_FILE_PATH:
                return contentUri;
            case TYPE_FILE_URI:
                return toFilePath(contentUri);
            case TYPE_HTTP:
            case TYPE_NULL:
            case TYPE_OTHER:
                return null;
        }
        return null;
    }

    private static String resolveContentUriPath(Context context, String contentUri, File storeDirectory) {
        String[] projections = new String[]{MediaStore.MediaColumns.DATA};
        Uri uri = Uri.parse(contentUri);
        String filePath = null;
        String fileName = null;
        File file = null;
        Cursor cursor = context.getContentResolver().query(uri, projections, null, null, null);
        if(cursor != null) {
            try {
                cursor.moveToFirst();
                int index = cursor.getColumnIndex(MediaStore.MediaColumns.DATA);
                if(index != -1) {
                    filePath = cursor.getString(index);
                    fileName = getFileName(filePath);
                }
                if(filePath != null) {
                    file = new File(filePath);
                }
                if(file == null || !file.exists() || file.length() == 0) {
                    if(fileName == null) {
                        fileName = getFileName(contentUri);
                    }
                    if(fileName == null) {
                        fileName = SocialUtils.md5(contentUri);
                    }
                    file = storeToLocalFile(context, uri, storeDirectory, fileName);
                }
            } finally {
                cursor.close();
            }
        }
        return file != null && file.exists() ? file.getAbsolutePath() : null;
    }

    private static File storeToLocalFile(Context context, Uri uri, File storeDirectory, String fileName) {
        InputStream inputStream = null;
        BufferedOutputStream bis = null;
        try {
            inputStream = context.getContentResolver().openInputStream(uri);
            File destFile;
            if (storeDirectory != null) {
                if(!storeDirectory.exists()) {
                    storeDirectory.mkdirs();
                }
                destFile = new File(storeDirectory, fileName);
            } else {
                destFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);
            }
            if(destFile.exists()) {
                destFile.delete();
            }

            byte[] buffer = new byte[8192];
            int len;
            bis = new BufferedOutputStream(new FileOutputStream(destFile), 8192);
            while ((len = inputStream.read(buffer)) > 0) {
                bis.write(buffer, 0, len);
            }
            bis.flush();
            return destFile;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            SocialUtils.closeStream(inputStream);
            SocialUtils.closeStream(bis);
        }
        return null;
    }

    public static String getFileName(String uri) {
        return uri != null ? Uri.parse(uri).getLastPathSegment() : null;
    }

    public static @UriType int getTargetType(@UriType int acceptTypes, @UriType int uriType) {
        if(acceptType(acceptTypes, uriType) == uriType) return uriType;
        switch (uriType) {
            case TYPE_CONTENT_URI:
                return acceptType(acceptTypes, TYPE_FILE_PATH, TYPE_FILE_URI);
            case TYPE_FILE_PATH:
                return acceptType(acceptTypes, TYPE_FILE_URI, TYPE_CONTENT_URI);
            case TYPE_FILE_URI:
                return acceptType(acceptTypes, TYPE_FILE_PATH, TYPE_CONTENT_URI);
            case TYPE_HTTP:
                return acceptType(acceptTypes, TYPE_FILE_PATH, TYPE_FILE_URI, TYPE_CONTENT_URI);
            case TYPE_NULL:
            case TYPE_OTHER:
                return TYPE_NULL;
        }
        return TYPE_NULL;
    }

    private static int acceptType(@UriType int acceptTypes, int... types) {
        for (int i = 0; i < types.length; i++) {
            if((types[i] & acceptTypes) == types[i]) {
                return types[i];
            }
        }
        return TYPE_NULL;
    }

    public static @UriType int getUriType(String uri) {
        if(TextUtils.isEmpty(uri))
            return TYPE_NULL;
        if(uri.startsWith("content://"))
            return TYPE_CONTENT_URI;
        if(uri.startsWith("file://"))
            return TYPE_FILE_URI;
        if(uri.startsWith("/"))
            return TYPE_FILE_PATH;
        if(uri.startsWith("http"))
            return TYPE_HTTP;
        return TYPE_OTHER;
    }

    public static boolean containType(@UriType int types, @UriType int type) {
        return type != TYPE_NULL && (type & types) == type;
    }

    public static Uri getContentUri(Context context, File file) {
        return FileProvider.getUriForFile(context, context.getPackageName() + ".rxsocial.fileprovider", file);
    }
}
