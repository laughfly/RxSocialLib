package com.laughfly.rxsociallib.plugin

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Created by caowy on 2019/4/8.
 * email:cwy.fly2@gmail.com
 */
class SocialPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        SocialConfigExtension config = project.extensions.create("RxSocialConfig", SocialConfigExtension)
        config.project = project
        if (project.plugins.hasPlugin(AppPlugin)) {
            PlatformConfigTransform transform = new PlatformConfigTransform(project)
            project.extensions.getByType(AppExtension).registerTransform(transform)
        }
    }

}
