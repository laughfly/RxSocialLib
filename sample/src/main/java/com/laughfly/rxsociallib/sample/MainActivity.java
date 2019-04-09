package com.laughfly.rxsociallib.sample;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.laughfly.rxsociallib.Platform;
import com.laughfly.rxsociallib.RxSocial;
import com.laughfly.rxsociallib.login.SocialLoginResult;
import com.laughfly.rxsociallib.share.SocialShareResult;

import rx.Subscriber;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }

    public void shareTo(View view) {
        Platform platform = Platform.QQ;
        switch (view.getId()) {
            case R.id.qq_share:
                platform = Platform.QQ;
                break;
            case R.id.weixin_share:
                platform = Platform.Wechat;
                break;
            case R.id.weibo_share:
                platform = Platform.Weibo;
                break;
        }
        RxSocial.share(this)
            .setPlatform(platform)
            .setTitle("分享标题")
            .setText("分享的内容").setPageUrl("http://www.qq.com")
            .toObservable()
            .subscribe(new Subscriber<SocialShareResult>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {
                    Toast.makeText(MainActivity.this, "分享失败", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onNext(SocialShareResult socialShareResult) {
                    Toast.makeText(MainActivity.this, "分享成功", Toast.LENGTH_LONG).show();
                }
            });
    }

    public void loginWith(View view) {
        Platform platform = Platform.QQ;
        switch (view.getId()) {
            case R.id.qq_login:
                platform = Platform.QQ;
                break;
            case R.id.weixin_login:
                platform = Platform.Wechat;
                break;
            case R.id.weibo_login:
                platform = Platform.Weibo;
                break;
        }
        RxSocial.login(this)
            .setPlatform(platform)
            .toObservable()
            .subscribe(new Subscriber<SocialLoginResult>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {
                    Toast.makeText(MainActivity.this, "登录失败", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onNext(SocialLoginResult socialLoginResult) {
                    Toast.makeText(MainActivity.this, "登录成功\nuid: " + socialLoginResult.uid, Toast.LENGTH_LONG).show();
                }
            });
    }
}
