package com.laughfly.rxsociallib;

import android.content.Context;
import android.net.Uri;

import com.laughfly.rxsociallib.internal.AccessToken;
import com.laughfly.rxsociallib.internal.AccessTokenKeeper;
import com.laughfly.rxsociallib.login.LoginBuilder;
import com.laughfly.rxsociallib.share.ShareBuilder;

import java.io.InputStream;
import java.util.HashMap;

/**
 * 社会化工具类
 * author:caowy
 * date:2018-04-25
 */
public class RxSocial {

    /**
     * 平台配置
     */
    private static final SocialConfig sSocialConfig = new SocialConfig();

    public static void setSocialConfig(InputStream configStream) {
        HashMap<Platform, PlatformConfig> configMap = ConfigParser.readFromStream(configStream);
        if (configMap != null) {
            sSocialConfig.setPlatformConfigs(configMap);
        }
    }

    /**
     * 从res或本地文件读取配置
     * @param context
     * @param configUri
     */
    public static void setSocialConfig(Context context, Uri configUri) {
        HashMap<Platform, PlatformConfig> configMap = ConfigParser.readFromUri(context, configUri);
        if (configMap != null) {
            sSocialConfig.setPlatformConfigs(configMap);
        }
    }

    /**
     * 从文本读取配置，适用于从服务器获取配置
     * @param jsonText
     */
    public static void setSocialConfig(String jsonText) {
        HashMap<Platform, PlatformConfig> configMap = ConfigParser.readFromText(jsonText);
        if (configMap != null) {
            sSocialConfig.setPlatformConfigs(configMap);
        }
    }

    /**
     * 设置单个平台的配置
     * @param platform
     * @param config
     */
    public static void setPlatformConfig(Platform platform, PlatformConfig config) {
        sSocialConfig.addPlatformConfig(platform, config);
    }

    /**
     * 登录
     * author:caowy
     * date:2018-04-25
     *
     * @param context
     * @param platform
     * @return
     */
    public static LoginBuilder login(Context context, Platform platform) {
        return new LoginBuilder(context, platform, sSocialConfig.getPlatformConfig(platform));
    }

    /**
     * 分享
     * author:caowy
     * date:2018-04-25
     *
     * @param context
     * @param platform
     * @return
     */
    public static ShareBuilder share(Context context, Platform platform) {
        return new ShareBuilder(context, platform, sSocialConfig.getPlatformConfig(platform));
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
    public static AccessToken getAccessToken(Context context, Platform platform) {
        return AccessTokenKeeper.readAccessToken(context, platform);
    }

}
