package com.laughfly.rxsociallib.plugin

import org.gradle.api.Project

/**
 * Created by caowy on 2019/4/8.
 * email:cwy.fly2@gmail.com
 */

class SocialConfigExtension {
    static Set platforms = ["Weibo", "Wechat", "WechatMoments", "QQ", "QQZone"]

    Project project
    ConfigGenerator generator
    String libVersion

    void setProject(Project project) {
        this.project = project
        generator = new ConfigGenerator()
        generator.project = project
        generator.config = this
        generator.generate()
    }

    def methodMissing(String name, def args) {
        def platform = platforms.find{
            return name.equalsIgnoreCase(it)
        }
        if (platform != null) {
            PlatformConfigExtension info = new PlatformConfigExtension(args[0])
            info.fields.put("platform", platform)
            generator.platformInfoMap.put(platform, info)
        } else if ("libVersion".equalsIgnoreCase(name)) {
            libVersion = args[0]
        }
    }

}
