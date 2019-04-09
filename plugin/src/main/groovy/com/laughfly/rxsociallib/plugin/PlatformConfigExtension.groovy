package com.laughfly.rxsociallib.plugin;

/**
 * 平台配置信息
 * Created by caowy on 2019/4/8.
 * email:cwy.fly2@gmail.com
 */
class PlatformConfigExtension {
    static Set fieldNames = ["appId", "appSecret", "redirectUrl", "scope", "state"]
    Map fields = [:]

    PlatformConfigExtension(Closure c) {
        c.resolveStrategy = Closure.DELEGATE_FIRST
        c.delegate  = this
        c()
    }

    def methodMissing(String name, def args) {
        String key = fieldNames.find {
            return name.equalsIgnoreCase(it)
        }
        if (key == null) {
            this."${name}" = args[0]
        } else {
            this."${key}" = args[0]
        }
        return null
    }

    def propertyMissing(String name, def arg) {
        String key = fieldNames.find {
            return name.equalsIgnoreCase(it)
        }
        if (key == null) {
            return fields.put(name, arg)
        } else {
            return fields.put(key, arg)
        }
    }

    Map getFields() {
        return fields
    }
}
