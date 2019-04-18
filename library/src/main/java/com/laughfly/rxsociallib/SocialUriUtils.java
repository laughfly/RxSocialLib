package com.laughfly.rxsociallib;

import android.net.Uri;
import android.support.annotation.IntDef;
import android.text.TextUtils;

import java.io.File;

/**
 * Created by caowy on 2019/4/16.
 * email:cwy.fly2@gmail.com
 */

public class SocialUriUtils {
    public static final int TYPE_FILE_URI = 1;

    public static final int TYPE_FILE_PATH = 2;

    public static final int TYPE_CONTENT_URI = 3;

    public static final int TYPE_HTTP = 4;

    public static final int TYPE_OTHER = 5;

    @IntDef(value = {TYPE_FILE_PATH, TYPE_FILE_URI, TYPE_CONTENT_URI, TYPE_HTTP, TYPE_OTHER})
    @interface UriType {}

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
        return Uri.fromFile(new File(filePath)).toString();
    }

    public static String getFileName(String uri) {
        return new File(uri).getName();
    }

    public static @UriType int getUriType(String uri) {
        if(TextUtils.isEmpty(uri))
            return TYPE_OTHER;
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


}
