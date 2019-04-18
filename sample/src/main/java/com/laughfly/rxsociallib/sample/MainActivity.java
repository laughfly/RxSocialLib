package com.laughfly.rxsociallib.sample;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.laughfly.rxsociallib.RxSocial;
import com.laughfly.rxsociallib.SocialCallback;
import com.laughfly.rxsociallib.exception.SocialException;
import com.laughfly.rxsociallib.login.LoginPlatform;
import com.laughfly.rxsociallib.login.SocialLoginResult;
import com.laughfly.rxsociallib.share.SharePlatform;
import com.laughfly.rxsociallib.share.SocialShareResult;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
        RxSocial.initialize(this);
    }

    public void shareTo(View view) {
        String platform = SharePlatform.QQ;
        switch (view.getId()) {
            case R.id.qq_share:
                platform = SharePlatform.QQ;
                break;
            case R.id.weixin_share:
                platform = SharePlatform.Wechat;
                break;
            case R.id.weibo_share:
                platform = SharePlatform.Weibo;
                break;
        }
        RxSocial.share(this)
            .setPlatform(platform)
            .setTitle("我是标题")
            .setText("文本内容")
            .setPageUrl("http://www.qq.com/")
            .start(new SocialCallback<SocialShareResult>() {

                @Override
                public void onError(String platform, SocialException e) {
                    Toast.makeText(MainActivity.this, "分享失败: " + e.getErrCode(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(String platform, SocialShareResult resp) {
                    Toast.makeText(MainActivity.this, "分享成功", Toast.LENGTH_SHORT).show();
                }
            });
    }

    public void loginWith(View view) {
        String platform = LoginPlatform.QQ;
        switch (view.getId()) {
            case R.id.qq_login:
                platform = LoginPlatform.QQ;
                break;
            case R.id.weixin_login:
                platform = LoginPlatform.Wechat;
                break;
            case R.id.weibo_login:
                platform = LoginPlatform.Weibo;
                break;
        }
        RxSocial.login(this)
            .setPlatform(platform)
            .start(new SocialCallback<SocialLoginResult>() {
                @Override
                public void onError(String platform, SocialException e) {
                    Toast.makeText(MainActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(String platform, SocialLoginResult resp) {
                    Toast.makeText(MainActivity.this, "登录成功：" + resp.uid, Toast.LENGTH_SHORT).show();
                }
            });
    }
}
