package com.laughfly.rxsociallib;

import java.util.HashMap;

/**
 * 平台配置
 * author:caowy
 * date:2018-04-20
 */
public class SocialConfig {

    public static final int WECHAT_IMAGE_LIMIT = 10485760;
    public static final int WECHAT_MINI_PROG_IMAGE_LIMIT = 128 * 1024;
    public static final int WECHAT_THUMB_LIMIT = '耀';

    private static HashMap<Platform, PlatformConfig> mConfigMap = new HashMap<>();

    private static boolean mInitialized;

    public static PlatformConfig getPlatformConfig(Platform platform) {
        return mConfigMap.get(platform);
    }

    static void setPlatformConfigs(HashMap<Platform, PlatformConfig> configs) {
        mConfigMap.clear();
        mConfigMap.putAll(configs);
        mInitialized = true;
    }

    static boolean isInitialized() {
        return mInitialized;
    }
}
