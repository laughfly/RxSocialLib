package com.laughfly.rxsociallib.sample;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.laughfly.rxsociallib.share.ShareType;

import java.util.ArrayList;

/**
 * Created by caowy on 2019/4/19.
 * email:cwy.fly2@gmail.com
 */

class TestData {
    static String getTitle(@ShareType.Def int type) {
        return "分享的标题";
    }

    static String getText(@ShareType.Def int type) {
        return "分享的内容";
    }

    static String getWebUrl(@ShareType.Def int type) {
        return "http://www.github.com/";
    }

    static String getImageUri(@ShareType.Def int type) {
        return "https://gitee.com/laughfly/ShareFiles/raw/master/images/image_1.jpg";
//        return "content://media/external/images/media/187875";
//        return "/storage/emulated/0/Download/202532yuulnk3rvnwypsnk.jpg";
//        return "content://media/external/images/media/187874";
//        return "/storage/emulated/0/Download/8b4e89f9cba2eff636e090404480158f.jpg";
    }

    static int getImageResId() {
        return R.mipmap.image_1;
    }

    static Bitmap getImageBitmap(Context context) {
        return BitmapFactory.decodeResource(context.getResources(), R.mipmap.image_1);
    }

    static String getThumbUri(@ShareType.Def int type) {
//        return "https://gitee.com/laughfly/ShareFiles/raw/master/images/thumb_1.png";
//        return "/storage/emulated/0/Download/8b4e89f9cba2eff636e090404480158f.jpg";
        return "content://media/external/images/media/187874";
    }

    static int getThumbResId() {
        return R.mipmap.thumb_1;
    }

    static Bitmap getThumbBitmap(Context context) {
        return BitmapFactory.decodeResource(context.getResources(), R.mipmap.thumb_1);
    }

    static String getAudioUri() {
        return "https://gitee.com/laughfly/ShareFiles/raw/master/audios/audio_1.jpg";
    }

    static String getVideoUri(@ShareType.Def int type) {
        if(ShareType.SHARE_NETWORK_VIDEO == type) {
            return "https://gitee.com/laughfly/ShareFiles/raw/master/videos/video_1.jpg";
        } else {
            return "content://media/external/video/media/35510";
//            return "/storage/emulated/0/Download/Rip-Balls-we-had-so-mich-fun-together.mp4";
        }
    }

    static String getFileUri() {
        return "content://media/external/images/media/185656";
    }

    static String getAppInfo() {
        return "{app:com.tencent.music,view:Share,meta:{Share:{musicId:4893051}}}";
    }

    static ArrayList<String> getImageList() {
        ArrayList<String> imageList = new ArrayList<>();
        imageList.add("content://media/external/images/media/185656");
        imageList.add("/storage/emulated/0/1530110050129.jpg");
        imageList.add("https://gitee.com/laughfly/ShareFiles/raw/master/images/image_1.jpg");
        return imageList;
    }

    static ArrayList<String> getFileList() {
        ArrayList<String> fileList = new ArrayList<>();
        fileList.add("/storage/emulated/0/Download/Rip-Balls-we-had-so-mich-fun-together.mp4");
        fileList.add("/storage/emulated/0/LSSportrelease/log/BleLog/startDevice.txt");
        fileList.add("https://gitee.com/laughfly/ShareFiles/raw/master/images/image_1.jpg");
        return fileList;
    }

    static String getMiniProgramUser() {
        return "";
    }

    static String getMiniProgramPath() {
        return "";
    }

    static int getMiniProgramType() {
        return 0;
    }
}
