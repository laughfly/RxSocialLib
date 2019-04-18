package com.laughfly.rxsociallib.platform.qq;

import android.content.Context;

import com.laughfly.rxsociallib.SocialUtils;

/**
 * Created by caowy on 2019/4/16.
 * email:cwy.fly2@gmail.com
 */

public class QQUtils {
    public static boolean isQQInstalled(Context context) {
        return SocialUtils.checkAppInstalled(context, QQConstants.QQ_PACKAGE_NAME);
    }

    public static boolean isTimInstalled(Context context) {
        return SocialUtils.checkAppInstalled(context, QQConstants.TIM_PACKAGE_NAME);
    }
}
