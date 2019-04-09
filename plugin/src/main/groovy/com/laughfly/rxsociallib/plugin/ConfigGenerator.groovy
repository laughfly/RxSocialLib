package com.laughfly.rxsociallib.plugin

import com.android.build.gradle.AppExtension
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.api.ApplicationVariant
import com.g00fy2.versioncompare.Version
import groovy.json.JsonOutput
import groovy.xml.XmlUtil
import org.gradle.api.Project

/**
 * 生成平台配置和AndroidManifest.xml
 * Created by caowy on 2019/4/8.
 * email:cwy.fly2@gmail.com
 */

class ConfigGenerator {
    Project project
    SocialConfigExtension config
    String gradlePluginVersion

    Map<String, PlatformConfigExtension> platformInfoMap = [:]

    void generate() {
        gradlePluginVersion = getAndroidGradlePluginVersionCompat()
        println("gradlePluginVersion: " + gradlePluginVersion)
        project.afterEvaluate {
            println("SocialLibVersion: " + config.libVersion)
            project.dependencies {
                def compile = checkGradleVersion("3.0") ? "implementation" : "compile"
                if (config.libVersion != null) {
                    add(compile, "com.laughfly.rxsociallib:rxsocial:${config.libVersion}")
                } else {
                    add(compile, "com.laughfly.rxsociallib:rxsocial:+")
                }
            }

            BaseExtension android = project.extensions.getByName("android")
            generateConfigJson(android)

            def variants = getVariants(android)
            if (variants != null) {
                variants.all { variant ->
                    variant.outputs.each { output ->
                        try {
                            def task = output.processManifest
                            if (task != null) {
                                task.doLast {
                                    generateConfigManifest(output, variant)
                                }
                            }
                        } catch (Throwable t) {
                        }
                    }
                }
            }
        }
    }

    void generateConfigJson(BaseExtension android) {
        if (!platformInfoMap.isEmpty()) {
            def builder = new StringBuilder()
            builder.append('[')
            platformInfoMap.each { platform, info ->
                if (info != null) {
                    if (builder.length() > 1) {
                        builder.append(',')
                    }
                    def json = JsonOutput.toJson(info.fields)
                    builder.append(json)
                }
            }
            builder.append(']')
            String fileName = "rxsocial_config.json"
            File assetsDir = findAssetsDir(android, fileName)
            File jsonFile = new File(assetsDir, fileName)
            jsonFile.setText(builder.toString(), "UTF-8")
        }
    }

    void generateConfigManifest(def output, def variant) {
        String packageName = getPackageName(variant)
        def manifestFiles = getManifestOutputFile(output)

        manifestFiles.each { manifestFile ->
            if (manifestFile != null && manifestFile.exists()) {
                def parser = new XmlSlurper()
                def manifest = parser.parse(manifestFile)
                def application = manifest.'application'[0]
                boolean wechatAdded = false
                boolean qqAdded = false
                platformInfoMap.each { platform, info ->
                    if (!wechatAdded && platform.toLowerCase().startsWith("wechat")) {
                        def targetCls
                        if (packageName != null) {
                            targetCls = "${packageName}" + ".wxapi.WXEntryActivity"
                        } else {
                            targetCls = '${applicationId}' + ".wxapi.WXEntryActivity"
                        }
                        def toAdd = """
                                    <activity-alias
                                        xmlns:android="http://schemas.android.com/apk/res/android"
                                        xmlns:tools="http://schemas.android.com/tools"
                                        android:name="${targetCls}"
                                        android:exported="true"
                                        android:targetActivity="com.laughfly.rxsociallib.platform.wechat.WechatDelegateActivity"/>
                                    """
                        application.appendNode(parser.parseText(toAdd))
                        wechatAdded = true
                    }
                    if (!qqAdded && platform.toLowerCase().startsWith("qq")) {
                        def toAdd = """
                                    <activity
                                        xmlns:android="http://schemas.android.com/apk/res/android"
                                        xmlns:tools="http://schemas.android.com/tools"
                                        android:name="com.tencent.tauth.AuthActivity"
                                        android:configChanges="orientation|screenSize|screenLayout|keyboardHidden|navigation"
                                        android:launchMode="singleTask"
                                        android:noHistory="true"
                                        android:screenOrientation="portrait">
                                        <intent-filter>
                                            <action android:name="android.intent.action.VIEW"/>
                                            <category android:name="android.intent.category.DEFAULT"/>
                                            <category android:name="android.intent.category.BROWSABLE"/>
                                            <data android:scheme="tencent${info.fields.get('appId')}" />
                                        </intent-filter>
                                    </activity>
                                    """
                        application.appendNode(parser.parseText(toAdd))
                        qqAdded = true
                    }
                }
                manifestFile.setText(XmlUtil.serialize(manifest), "utf-8")
            }
        }
    }

    /**
     * GitHub:https://github.com/lizhangqu/AndroidGradlePluginCompat
     * 导出获得android gradle plugin插件的版本号，build.gradle中apply后可直接使用getAndroidGradlePluginVersionCompat()
     */
    @SuppressWarnings("GrMethodMayBeStatic")
    String getAndroidGradlePluginVersionCompat() {
        String version
        try {
            Class versionModel = Class.forName("com.android.builder.model.Version")
            def versionFiled = versionModel.getDeclaredField("ANDROID_GRADLE_PLUGIN_VERSION")
            versionFiled.setAccessible(true)
            version = versionFiled.get(null)
        } catch (Exception e) {
            version = "unknown"
        }
        return version
    }

    boolean checkGradleVersion(String targetVersion) {
        return new Version(gradlePluginVersion).isAtLeast(targetVersion)
    }

    def getVariants(def android) {
        def variants = null
        if (android instanceof AppExtension) {
            variants = android.applicationVariants
        } else if (android instanceof LibraryExtension){
            variants = android.libraryVariants
        }
        return variants
    }

    boolean isAppMode(def android) {
        return android in AppExtension
    }

    def getProcessManifestTask(def output) {
        def result = null
        try {
            result = output.getProcessManifestProvider().get()
        } catch (Throwable t) {
            try {
                result = output.getProcessManifest()
            } catch (Throwable t2) {
            }
        }
        return result
    }

    def getPackageName(def variant) {
        if (variant instanceof ApplicationVariant) {
            return variant.applicationId
        }
        return null
    }

    def getManifestOutputFile(def output) {
        def manifestFiles = []
        try {
            manifestFiles.add(new File(getProcessManifestTask(output).manifestOutputDirectory.get().getAsFile(), "AndroidManifest.xml"))
        } catch (Throwable t) {
            try {
                manifestFiles.add(new File(getProcessManifestTask(output).manifestOutputDirectory, "AndroidManifest.xml"))
            } catch (Throwable t2) {
                try {
                    def file = getProcessManifestTask(output).manifestOutputFile
                    if (file.exists()) {
                        manifestFiles.add(file)
                    }
                } catch (Throwable t3) {
                }
            }
        }
        try {
            manifestFiles.add(new File(getProcessManifestTask(output).instantRunManifestOutputDirectory, "AndroidManifest.xml"))
        } catch (Throwable t) {
            try {
                manifestFiles.add(getProcessManifestTask(output).instantRunManifestOutputFile)
            } catch (Throwable tt) {
            }
        }
        return manifestFiles
    }

    File findAssetsDir(BaseExtension android, String targetFile) {
        def assetsDir = null
        def assetsDirs = android.sourceSets.main.assets.srcDirs
        assetsDirs.each { dir ->
            for (name in dir.list()) {
                if (targetFile.equals(name)) {
                    assetsDir = dir
                    new File(dir, name).delete()
                    break
                }
            }
        }

        if (assetsDir == null) {
            String assetsFolder = "intermediates${File.separator}RxShareLib${File.separator}assets"
            assetsDir = new File(project.rootProject.buildDir, assetsFolder)
            if (assetsDirs.size() > 0) {
                HashSet set = new HashSet()
                set.addAll(assetsDirs)
                set.add(assetsDir)
                android.sourceSets.main.assets.srcDirs = set
            } else {
                android.sourceSets.main.assets.srcDirs = [assetsDir]
            }
        }

        if (!assetsDir.exists()) {
            assetsDir.mkdirs()
        }
        return assetsDir
    }
}
