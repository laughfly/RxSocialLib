package com.laughfly.rxsociallib;

import org.json.JSONObject;

public class AccessToken {

    public String accessToken;
    public String openId;
    public String uid;
    public String refreshToken;
    public long expiresIn;

    @Override
    public String toString() {
        return "AccessToken{" +
            "accessToken='" + accessToken + '\'' +
            ", openId='" + openId + '\'' +
            ", uid='" + uid + '\'' +
            ", refreshToken='" + refreshToken + '\'' +
            ", expiresIn=" + expiresIn +
            '}';
    }

    public static String toJson(AccessToken token) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("access_token", token.accessToken);
            jsonObject.put("open_id", token.openId);
            jsonObject.put("uid", token.uid);
            jsonObject.put("refresh_token", token.refreshToken);
            jsonObject.put("expires_in", token.expiresIn);
            return jsonObject.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static AccessToken fromJson(String jsonStr) {
        if(jsonStr == null) return null;
        try {
            JSONObject json = new JSONObject(jsonStr);
            AccessToken token = new AccessToken();
            token.accessToken = json.optString("access_token");
            token.openId = json.optString("open_id");
            token.uid = json.optString("uid");
            token.refreshToken = json.optString("refresh_token");
            token.expiresIn = json.optLong("expires_in");
            return token;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
