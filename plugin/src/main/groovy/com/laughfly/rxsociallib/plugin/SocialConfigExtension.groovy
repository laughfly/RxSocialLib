package com.laughfly.rxsociallib.plugin

import com.android.build.gradle.AppExtension
import org.gradle.api.Project

/**
 * Created by caowy on 2019/4/8.
 * email:cwy.fly2@gmail.com
 */

class SocialConfigExtension {
    static Map<String, String> platformLibMap = ['Weibo':'weibo', 'WeiboStory':'weibo',
                                                 'Wechat':'wechat', 'WechatMoments':'wechat',
                                                 'QQ':'qq', 'QQZone':'qq']

    Project project
    ConfigGenerator generator
    String libVersion
    boolean debug

    void setProject(Project project) {
        this.project = project
        generator = new ConfigGenerator()
        generator.project = project
        generator.config = this
        generator.generate()

        PlatformConfigTransform transform = new PlatformConfigTransform(project, generator)
        project.extensions.getByType(AppExtension).registerTransform(transform)
    }

    def methodMissing(String name, def args) {
        def platform = platformLibMap.keySet().find{
            return name.equalsIgnoreCase(it)
        }
        if (platform != null) {
            PlatformConfigExtension info = new PlatformConfigExtension(args[0])
            info.fields.put("platform", platform)
            generator.platformInfoMap.put(platform, info)
            generator.platformLibs.add(platformLibMap.get(platform))
        } else if ("libVersion".equalsIgnoreCase(name)) {
            libVersion = args[0]
        } else if ("debug".equalsIgnoreCase(name)) {
            debug = args[0]
        }
    }

}
