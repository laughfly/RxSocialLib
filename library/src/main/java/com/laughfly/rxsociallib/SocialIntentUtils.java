package com.laughfly.rxsociallib;

import android.content.Intent;
import android.net.Uri;

import java.util.ArrayList;

/**
 * Created by caowy on 2019/4/16.
 * email:cwy.fly2@gmail.com
 */
public class SocialIntentUtils {

    public static Intent createTextShare(String title, String text) {
        return createTextShare(title, text, null, null);
    }

    public static Intent createTextShare(String title, String text, String pkg, String targetCls) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        intent.putExtra(Intent.EXTRA_TITLE, title);
        intent.setType("text/plain");
        setComponent(intent, pkg, targetCls);
        return intent;
    }

    public static Intent createImageListShare(ArrayList<Uri> imageUriList) {
        return createImageListShare(imageUriList, null, null);
    }

    public static Intent createImageListShare(ArrayList<Uri> imageUriList, String pkg, String targetCls) {
        return createFileListShare(imageUriList, "image/*", pkg, targetCls);
    }

    public static Intent createVideoShare(Uri videoUri, String pkg, String targetCls) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setType("video/*");
        intent.putExtra(Intent.EXTRA_STREAM, videoUri);
        setComponent(intent, pkg, targetCls);
        return intent;
    }

    public static Intent createFileShare(Uri fileUri) {
        return createFileShare(fileUri, null, null);
    }

    public static Intent createFileShare(Uri fileUri, String pkg, String targetCls) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_STREAM, fileUri);
        setComponent(intent, pkg, targetCls);

        return intent;
    }

    public static Intent createFileListShare(ArrayList<Uri> fileUriList) {
        return createFileListShare(fileUriList, null, null);
    }

    public static Intent createFileListShare(ArrayList<Uri> fileUriList, String pkg, String targetCls) {
        return createFileListShare(fileUriList, "*/*", pkg, targetCls);
    }

    public static Intent createFileListShare(ArrayList<Uri> fileUriList, String mimeType, String pkg, String targetCls) {
        Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setType(mimeType);
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, fileUriList);
        setComponent(intent, pkg, targetCls);
        return intent;
    }

    private static void setComponent(Intent intent, String pkg, String targetCls) {
        if(pkg != null) {
            intent.setPackage(pkg);
            if (targetCls != null) {
                intent.setClassName(pkg, targetCls);
            }
        }
    }
}
