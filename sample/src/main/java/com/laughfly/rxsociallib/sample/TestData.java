package com.laughfly.rxsociallib.sample;

import com.laughfly.rxsociallib.share.ShareType;

import java.util.ArrayList;

/**
 * Created by caowy on 2019/4/19.
 * email:cwy.fly2@gmail.com
 */

class TestData {
    static String getTitle(@ShareType.Def int type) {
        return "分享的标题啦啦啦";
    }

    static String getText(@ShareType.Def int type) {
        return "分享的内容哈哈哈";
    }

    static String getWebUrl(@ShareType.Def int type) {
        return "http://www.github.com/";
    }

    static String getImageUri(@ShareType.Def int type) {
        return "https://gitee.com/laughfly/ShareFiles/raw/master/images/image_1.jpg";
    }

    static String getThumbUri(@ShareType.Def int type) {
        return "https://gitee.com/laughfly/ShareFiles/raw/master/images/thumb_1.png";
    }

    static String getAudioUri(@ShareType.Def int type) {
        return "https://gitee.com/laughfly/ShareFiles/raw/master/audios/audio_1.jpg";
    }

    static String getVideoUri(@ShareType.Def int type) {
        if(ShareType.SHARE_NETWORK_VIDEO == type) {
            return "https://gitee.com/laughfly/ShareFiles/raw/master/videos/video_1.jpg";
        } else {
            return "/storage/emulated/0/Download/Rip-Balls-we-had-so-mich-fun-together.mp4";
        }
    }

    static String getFileUri(@ShareType.Def int type) {
        return "content://media/external/images/media/185656";
    }

    static String getAppInfo(@ShareType.Def int type) {
        return "{app:com.tencent.music,view:Share,meta:{Share:{musicId:4893051}}}";
    }

    static ArrayList<String> getImageList(@ShareType.Def int type) {
        ArrayList<String> imageList = new ArrayList<>();
        imageList.add("content://media/external/images/media/185656");
        imageList.add("/storage/emulated/0/1530110050129.jpg");
        return imageList;
    }

    static ArrayList<String> getFileList(@ShareType.Def int type) {
        ArrayList<String> fileList = new ArrayList<>();
        fileList.add("/storage/emulated/0/Download/Rip-Balls-we-had-so-mich-fun-together.mp4");
        fileList.add("/storage/emulated/0/LSSportrelease/log/BleLog/startDevice.txt");
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
