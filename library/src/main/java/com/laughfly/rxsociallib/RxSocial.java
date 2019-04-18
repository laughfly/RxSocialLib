package com.laughfly.rxsociallib;

import android.content.Context;

import com.laughfly.rxsociallib.downloader.ImageDownloader;
import com.laughfly.rxsociallib.internal.AccessTokenKeeper;
import com.laughfly.rxsociallib.login.LoginBuilder;
import com.laughfly.rxsociallib.share.ShareBuilder;
import com.laughfly.rxsociallib.share.ShareType;

import java.io.File;
import java.util.Set;

/**
 * 社会化工具类
 * author:caowy
 * date:2018-04-25
 */
public class RxSocial {

    public synchronized static void initialize(Context context) {
        if(SocialModel.isInitialized()) return;
        SocialModel.initialize(context);
    }

    public static Set<String> getSharePlatformList() {
        return SocialModel.getSharePlatforms();
    }

    public static Set<String> getLoginPlatforms() {
        return SocialModel.getLoginPlatforms();
    }

    private static Set<String> getSupportPlatforms(@ShareType.Def int shareType) {
        return SocialModel.getSupportPlatforms(shareType);
    }

    public static @ShareType.Def int getShareTypes(String platform) {
        return SocialModel.getSupportShareTypes(platform);
    }

    public static void setDownloadDirectory(File directory) {
        SocialModel.setDownloadDirectory(directory);
    }

    public static void setImageDownloader(ImageDownloader downloader) {
        SocialModel.setImageDownloader(downloader);
    }

    /**
     * 登录
     * author:caowy
     * date:2018-04-25
     *
     * @param context
     * @return
     */
    public static LoginBuilderWrapper login(Context context) {
        initialize(context);
        return new LoginBuilderWrapper(context);
    }

    /**
     * 分享
     * author:caowy
     * date:2018-04-25
     *
     * @param context
     * @return
     */
    public static ShareBuilderWrapper share(Context context) {
        initialize(context);
        return new ShareBuilderWrapper(context);
    }


    /**
     * 获取社交平台的登录信息
     * author:caowy
     * date:2018-04-25
     *
     * @param context
     * @param platform
     * @return
     */
    public static AccessToken getAccessToken(Context context, String platform) {
        return AccessTokenKeeper.readAccessToken(context, platform);
    }

    public static class ShareBuilderWrapper {
        Context mContext;

        ShareBuilderWrapper(Context context) {
            mContext = context;
        }

        public ShareBuilder setPlatform(String platform) {
            return new ShareBuilder(mContext, platform, SocialModel.getPlatformConfig(platform));
        }
    }

    public static class LoginBuilderWrapper {
        Context mContext;

        LoginBuilderWrapper(Context context) {
            mContext = context;
        }

        public LoginBuilder setPlatform(String platform) {
            return new LoginBuilder(mContext, platform, SocialModel.getPlatformConfig(platform));
        }
    }
}
