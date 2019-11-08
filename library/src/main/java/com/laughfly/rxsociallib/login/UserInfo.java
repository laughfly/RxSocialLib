package com.laughfly.rxsociallib.login;

import androidx.annotation.IntDef;

/**
 * Created by caowy on 2019/4/2.
 * email:cwy.fly2@gmail.com
 */

public class UserInfo {
    public static final int GENDER_FEMALE = 0;
    public static final int GENDER_MALE = 1;
    public static final int GENDER_UNKNOWN = 2;

    @IntDef(value = {GENDER_FEMALE, GENDER_MALE, GENDER_UNKNOWN})
    public @interface Gender {}

    public String nickname;

    public @Gender int gender;

    public String avatarUrl;

    public String signature;

}
