package com.laughfly.rxsociallib.login;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by caowy on 2019/4/11.
 * email:cwy.fly2@gmail.com
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface LoginFeatures {
    LoginFeature[] value();
}
