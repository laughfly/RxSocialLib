/*
 * Copyright (C) 2010-2013 The SINA WEIBO Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.laughfly.rxsociallib.internal;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.laughfly.rxsociallib.Platform;

/**
 * 存取各社交平台的登录信息
 * author:caowy
 * date:2018-04-25
 */
public class AccessTokenKeeper {
    private static final String PREFERENCES_NAME = "com_flytone_social_sdk";

    private static final String KEY_ACCESS_TOKEN = "access_token";

    /**
     * 清空 SharedPreferences 中 Token信息。
     *
     * @param context 应用程序上下文环境
     */
    public static void clear(Context context) {
        if (null == context) {
            return;
        }

        SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
        Editor editor = pref.edit();
        editor.clear();
        editor.commit();
    }

    public static void writeAccessToken(Context context, Platform platform, AccessToken accessToken) {
        if (null == context || null == accessToken) {
            return;
        }
        SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
        String json = AccessToken.toJson(accessToken);
        if(json != null) {
            pref.edit().putString(platformToKey(platform) + KEY_ACCESS_TOKEN, json).apply();
        }
    }

    public static AccessToken readAccessToken(Context context, Platform platform) {
        if(null == context) return null;
        SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
        String jsonStr = pref.getString(platformToKey(platform) + KEY_ACCESS_TOKEN, null);
        if(jsonStr != null) {
            AccessToken accessToken = AccessToken.fromJson(jsonStr);
            return accessToken;
        }
        return null;
    }

    private static String platformToKey(Platform platform) {
        switch (platform) {
            case QQ:
            case QQ_ZONE:
                return "qq";
            case WEIXIN:
            case WEIXIN_MOMENTS:
                return "Weixin";
            case WEIBO:
                return "weibo";
        }
        return "";
    }
}
