package com.laughfly.rxsociallib;

import android.content.Context;
import android.os.Environment;

import com.laughfly.rxsociallib.downloader.DefaultFileDownloader;
import com.laughfly.rxsociallib.downloader.FileDownloader;
import com.laughfly.rxsociallib.login.LoginAction;
import com.laughfly.rxsociallib.login.LoginFeature;
import com.laughfly.rxsociallib.login.LoginFeatures;
import com.laughfly.rxsociallib.share.ShareAction;
import com.laughfly.rxsociallib.share.ShareFeature;
import com.laughfly.rxsociallib.share.ShareFeatures;
import com.laughfly.rxsociallib.share.ShareType;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 平台配置
 * author:caowy
 * date:2018-04-20
 */
public class SocialModel {

    public static String FILE_CONFIG = "rxsocial_config.json";

    private static Context sApplicationContext;

    private static File sDownloadDirectory;

    private static FileDownloader sFileDownloader;

    private static HashMap<String, PlatformConfig> sConfigMap = new HashMap<>();

    private static HashMap<String, Class<? extends ShareAction>> sShareClassMap = new HashMap<>();

    private static HashMap<String, Integer> sShareSupportFeatures = new HashMap<>();

    private static HashMap<String, Class<? extends LoginAction>> sLoginClassMap = new HashMap<>();

    private static boolean sTreatNoResultAsSuccess;

    private static boolean sInitialized;

    static {
        sDownloadDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "RxSocial");
        sFileDownloader = new DefaultFileDownloader();
        sTreatNoResultAsSuccess = true;
    }

    public static Context getApplicationContext() {
        return sApplicationContext;
    }

    public static void setTreatNoResultAsSuccess(boolean treatNoResultAsSuccess) {
        sTreatNoResultAsSuccess = treatNoResultAsSuccess;
    }

    public static boolean getTreatNoResultAsSuccess() {
        return sTreatNoResultAsSuccess;
    }

    public static boolean getSupportLogin(String platform) {
        return sLoginClassMap.containsKey(platform);
    }

    public static Set<String> getSupportPlatforms(@ShareType.Def int shareType) {
        Set<String> supportPlatform = new HashSet<>();
        Set<String> platforms = sShareSupportFeatures.keySet();
        for (String platform : platforms) {
            int shareTypes = sShareSupportFeatures.get(platform);
            if((shareTypes & shareType) != 0) {
                supportPlatform.add(platform);
            }
        }
        return supportPlatform;
    }

    public static int getSupportShareTypes(String platform) {
        Integer shareTypes = sShareSupportFeatures.get(platform);
        return shareTypes != null ? shareTypes : 0;
    }

    public static Set<String> getSharePlatforms() {
        return sShareClassMap.keySet();
    }

    public static Set<String> getLoginPlatforms() {
        return sLoginClassMap.keySet();
    }

    public static PlatformConfig getPlatformConfig(String platform) {
        return sConfigMap.get(platform);
    }

    public static void setDownloadDirectory(File downloadDirectory) {
        sDownloadDirectory = downloadDirectory;
    }

    public static File getDownloadDirectory() {
        return sDownloadDirectory;
    }

    public static void setFileDownloader(FileDownloader fileDownloader) {
        sFileDownloader = fileDownloader;
    }

    public static FileDownloader getFileDownloader() {
        return sFileDownloader;
    }

    public static Class<? extends ShareAction> getShareClass(String platform) {
        return sShareClassMap.get(platform);
    }

    public static Class<? extends LoginAction> getLoginClass(String platform) {
        return sLoginClassMap.get(platform);
    }

    /**
     * 由插件按照配置生成
     * @return
     */
    public static List<Class> getSocialClassList() {
        return Collections.emptyList();
    }

    static boolean isInitialized() {
        return sInitialized;
    }

    static void initialize(Context context) {
        try {
            InputStream stream = context.getAssets().open(FILE_CONFIG);
            HashMap<String, PlatformConfig> configMap = ConfigParser.readFromStream(stream);
            if (configMap != null) {
                sConfigMap.putAll(configMap);
            }

            HashMap<String, Class<? extends ShareAction>> shareMap = new HashMap<>();
            HashMap<String, Integer> shareSupportFeatures = new HashMap<>();
            HashMap<String, Class<? extends LoginAction>> loginClassMap = new HashMap<>();

            List<Class> socialClassList = getSocialClassList();

            for (Class clazz : socialClassList) {
                ShareFeatures shareFeatures = (ShareFeatures) clazz.getAnnotation(ShareFeatures.class);
                if (shareFeatures != null) {
                    ShareFeature[] features = shareFeatures.value();
                    for (ShareFeature feature : features) {
                        String platform = feature.platform();
                        shareMap.put(platform, clazz);
                        shareSupportFeatures.put(platform, feature.supportFeatures());
                    }
                }
                LoginFeatures loginFeatures = (LoginFeatures) clazz.getAnnotation(LoginFeatures.class);
                if (loginFeatures != null) {
                    LoginFeature[] features = loginFeatures.value();
                    for (LoginFeature feature : features) {
                        String platform = feature.platform();
                        loginClassMap.put(platform, clazz);
                    }
                }
            }

            sShareClassMap.clear();
            sShareClassMap.putAll(shareMap);

            sShareSupportFeatures.clear();
            sShareSupportFeatures.putAll(shareSupportFeatures);

            sLoginClassMap.clear();
            sLoginClassMap.putAll(loginClassMap);

            sApplicationContext = context.getApplicationContext();
            sInitialized = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
