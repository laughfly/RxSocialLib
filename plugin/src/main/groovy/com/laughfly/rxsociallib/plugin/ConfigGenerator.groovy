package com.laughfly.rxsociallib.plugin

import com.android.build.gradle.*
import com.android.build.gradle.api.ApplicationVariant
import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.internal.api.BaseVariantImpl
import com.g00fy2.versioncompare.Version
import com.squareup.javapoet.FieldSpec
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.TypeSpec
import groovy.json.JsonOutput
import groovy.xml.XmlUtil
import org.gradle.api.Project

import javax.lang.model.element.Modifier

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
    Set platformLibs = []

    void generate() {
        gradlePluginVersion = getAndroidGradlePluginVersionCompat()
        println("gradlePluginVersion: " + gradlePluginVersion)

        def hasApp = project.plugins.hasPlugin AppPlugin
        def hasLib = project.plugins.hasPlugin LibraryPlugin
        if (!hasApp && !hasLib) {
            throw new IllegalStateException("'android' or 'android-library' plugin required.")
        }

        def variants
        if (hasApp) {
            variants = project.android.applicationVariants
        } else {
            variants = project.android.libraryVariants
        }

        generatePlatformClass(variants)

        project.afterEvaluate {
            addDependencies()

            BaseExtension android = project.extensions.getByName("android")

            generateConfigJson(android)

            variants.all { BaseVariant variant ->

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

    void addDependencies() {
        println(project.getDependencies().toString())
        project.dependencies {
            def scope = checkGradleVersion("3.0") ? "implementation" : "compile"
            def pluginVersion = getPluginVersion()
            println("SocialPluginVersion: " + pluginVersion)

            if (config.debug) {
            } else if (config.libVersion != null) {
                add(scope, "com.laughfly.rxsociallib:library:${config.libVersion}")
            } else {
                add(scope, "com.laughfly.rxsociallib:library:${pluginVersion}")
            }

            println("platformLibs size: " + platformLibs.size())
            platformLibs.each {
                if (config.debug) {
                } else if (config.libVersion != null) {
                    add(scope, "com.laughfly.rxsociallib:platform-${it.toLowerCase()}:${config.libVersion}")
                } else {
                    add(scope, "com.laughfly.rxsociallib:platform-${it.toLowerCase()}:${pluginVersion}")
                }
            }
        }
    }

    void generatePlatformClass(def variants) {
        variants.all { BaseVariantImpl variant ->
            def taskName = "generate${variant.name.capitalize()}SocialPlatform"
            def outputDir = project.file("${project.buildDir}/generated/source/rxsocial/${variant.name}/")
            def task = project.task(taskName)
            task.doFirst {
                TypeSpec.Builder shareBuilder = TypeSpec.interfaceBuilder("SharePlatform").addModifiers(Modifier.PUBLIC)
                TypeSpec.Builder loginBuilder = TypeSpec.interfaceBuilder("LoginPlatform").addModifiers(Modifier.PUBLIC)
                platformInfoMap.each {String platform, PlatformConfigExtension extension ->
                    if (extension.share) {
                        FieldSpec fieldSpec = FieldSpec.builder(String.class, platform, Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL).initializer("\$S", platform).build()
                        shareBuilder.addField(fieldSpec)
                    }
                    if (extension.login) {
                        FieldSpec fieldSpec = FieldSpec.builder(String.class, platform, Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL).initializer("\$S", platform).build()
                        loginBuilder.addField(fieldSpec)
                    }
                }
                def shareJavaFile = JavaFile.builder("com.laughfly.rxsociallib.share", shareBuilder.build()).build()
                shareJavaFile.writeTo(outputDir)
                def loginJavaFile = JavaFile.builder("com.laughfly.rxsociallib.login", loginBuilder.build()).build()
                loginJavaFile.writeTo(outputDir)
            }

            task.group = "other"

            String generateBuildConfigTaskName = variant.getVariantData().getScope().getGenerateBuildConfigTask().name
            def generateBuildConfigTask = project.tasks.getByName(generateBuildConfigTaskName)
            if (generateBuildConfigTask) {
                task.dependsOn generateBuildConfigTask
                generateBuildConfigTask.finalizedBy task
            }

            variant.registerJavaGeneratingTask(task, outputDir)
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
                                        android:targetActivity="com.laughfly.rxsociallib.platform.wechat.WechatEntryActivity"/>
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

    def getPluginVersion() {
        getClass().package?.implementationVersion == null ? '+' : getClass().package?.implementationVersion
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
        } else if (android instanceof LibraryExtension) {
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
