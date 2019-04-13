package com.laughfly.rxsociallib;

import android.content.Context;

import com.laughfly.rxsociallib.internal.AccessTokenKeeper;
import com.laughfly.rxsociallib.login.LoginBuilder;
import com.laughfly.rxsociallib.share.ShareBuilder;
import com.laughfly.rxsociallib.share.ShareFeature;

/**
 * 社会化工具类
 * author:caowy
 * date:2018-04-25
 */
public class RxSocial {

    private synchronized static void initSocialConfig(Context context) {
        if(SocialConfig.isInitialized()) return;
        SocialConfig.initialize(context);
    }

    private static String[] supportShareFeatures(@ShareFeature.Def int features) {
        return null;
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
        initSocialConfig(context);
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
        initSocialConfig(context);
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
            return new ShareBuilder(mContext, platform, SocialConfig.getPlatformConfig(platform));
        }
    }

    public static class LoginBuilderWrapper {
        Context mContext;

        LoginBuilderWrapper(Context context) {
            mContext = context;
        }

        public LoginBuilder setPlatform(String platform) {
            return new LoginBuilder(mContext, platform, SocialConfig.getPlatformConfig(platform));
        }
    }
}
