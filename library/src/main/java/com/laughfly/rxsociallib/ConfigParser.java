package com.laughfly.rxsociallib;

import android.content.Context;
import android.net.Uri;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;


/**
 * Created by caowy on 2019/3/27.
 * email:cwy.fly2@gmail.com
 */

class ConfigParser {

    public static final String FIELD_PLATFORM = "platform";

    public static final String FIELD_APPID = "appId";

    public static final String FIELD_APPSECRET = "appSecret";

    public static final String FIELD_SCOPE = "scope";

    public static final String FIELD_REDIRECT_URL = "redirectUrl";

    public static final String FIELD_STATE = "state";

    static HashMap<String, PlatformConfig> readFromUri(Context context, Uri configUri) {
        if (configUri != null) {
            try {
                InputStream inputStream = context.getContentResolver().openInputStream(configUri);
                return readFromStream(inputStream);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    static HashMap<String, PlatformConfig> readFromStream(InputStream inputStream) {
        if (inputStream != null) {
            String text = SocialUtils.readTextFromStream(inputStream);
            return readFromText(text);
        }
        return null;
    }

    static HashMap<String, PlatformConfig> readFromText(String jsonText) {
        try {
            HashMap<String, PlatformConfig> platformConfigs = new HashMap<>();
            JSONArray jsonArray = new JSONArray(jsonText);
            int size = jsonArray.length();
            for (int i = 0; i < size; i++) {
                PlatformConfig config = new PlatformConfig();

                JSONObject jsonObject = jsonArray.getJSONObject(i);

                config.appId = (jsonObject.optString(FIELD_APPID));
                config.appSecret = (jsonObject.optString(FIELD_APPSECRET));
                config.redirectUrl = (jsonObject.optString(FIELD_REDIRECT_URL));
                config.scope = (jsonObject.optString(FIELD_SCOPE));
                config.state = (jsonObject.optString(FIELD_STATE));

                String platform = jsonObject.optString(FIELD_PLATFORM);
                    platformConfigs.put(platform, config);
            }
            return platformConfigs;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

}
