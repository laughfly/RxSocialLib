package com.laughfly.rxsociallib.plugin

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
    }

}
