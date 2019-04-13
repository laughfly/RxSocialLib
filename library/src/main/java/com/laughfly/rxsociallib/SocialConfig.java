package com.laughfly.rxsociallib;

import android.content.Context;

import com.laughfly.rxsociallib.login.AbsSocialLogin;
import com.laughfly.rxsociallib.login.LoginFeature;
import com.laughfly.rxsociallib.login.LoginFeatures;
import com.laughfly.rxsociallib.share.AbsSocialShare;
import com.laughfly.rxsociallib.share.ShareFeature;
import com.laughfly.rxsociallib.share.ShareFeatures;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Set;

/**
 * 平台配置
 * author:caowy
 * date:2018-04-20
 */
public class SocialConfig {

    public static final int WECHAT_IMAGE_LIMIT = 10485760;
    public static final int WECHAT_MINI_PROG_IMAGE_LIMIT = 128 * 1024;
    public static final int WECHAT_THUMB_LIMIT = '耀';

    private static HashMap<String, PlatformConfig> sConfigMap = new HashMap<>();

    private static HashMap<String, Class<? extends AbsSocialShare>> sShareClassMap = new HashMap<>();

    private static HashMap<String, Integer> sShareSupportFeatures = new HashMap<>();

    private static HashMap<String, Class<? extends AbsSocialLogin>> sLoginClassMap = new HashMap<>();

    private static boolean mInitialized;

    public static PlatformConfig getPlatformConfig(String platform) {
        return sConfigMap.get(platform);
    }

    public static Class<? extends AbsSocialShare> getShareClass(String platform) {
        return sShareClassMap.get(platform);
    }

    public static Class<? extends AbsSocialLogin> getLoginClass(String platform) {
        return sLoginClassMap.get(platform);
    }

    public static int getShareSupportFeatures(String platform) {
        return sShareSupportFeatures.get(platform);
    }

    static boolean isInitialized() {
        return mInitialized;
    }

    static void initialize(Context context) {
        try {
            InputStream stream = context.getAssets().open("rxsocial_config.json");
            HashMap<String, PlatformConfig> configMap = ConfigParser.readFromStream(stream);
            if (configMap != null) {
                sConfigMap.putAll(configMap);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        HashMap<String, Class<? extends AbsSocialShare>> shareMap = new HashMap<>();
        HashMap<String, Integer> shareSupportFeatures = new HashMap<>();

        long time = System.currentTimeMillis();
        Set<Class<? extends AbsSocialShare>> subTypesOf = ClassUtils.getAllClassByInterface(AbsSocialShare.class, "com.laughfly.rxsociallib.platform");
        for (Class<? extends AbsSocialShare> type : subTypesOf) {
            ShareFeatures shareFeatures = type.getAnnotation(ShareFeatures.class);
            ShareFeature[] features = shareFeatures.value();
            for (ShareFeature feature : features) {
                String platform = feature.platform();
                shareMap.put(platform, type);
                shareSupportFeatures.put(platform, feature.supportFeatures());
            }
        }
        sShareClassMap.putAll(shareMap);
        sShareSupportFeatures.putAll(shareSupportFeatures);

        HashMap<String, Class<? extends AbsSocialLogin>> loginClassMap = new HashMap<>();
        Set<Class<? extends AbsSocialLogin>> subTypesOfLogin = ClassUtils.getAllClassByInterface(AbsSocialLogin.class, "com.laughfly.rxsociallib.platform");
        for (Class<? extends AbsSocialLogin> type : subTypesOfLogin) {
            LoginFeatures loginFeatures = type.getAnnotation(LoginFeatures.class);
            LoginFeature[] features = loginFeatures.value();
            for (LoginFeature feature : features) {
                String platform = feature.platform();
                loginClassMap.put(platform, type);
            }
        }
        sLoginClassMap.putAll(loginClassMap);

        Logger.w("RRRRRRROOOOOO", "REFTIME: " + (System.currentTimeMillis() - time));

        mInitialized = true;
    }
}
