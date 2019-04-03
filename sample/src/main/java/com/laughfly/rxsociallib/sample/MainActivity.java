package com.laughfly.rxsociallib.sample;

import android.os.Bundle;
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
        RxSocial.setSocialConfig(getResources().openRawResource(R.raw.social_config));
    }

    public void shareTo(View view) {
        Platform platform = Platform.QQ;
        switch (view.getId()) {
            case R.id.qq_share:
                platform = Platform.QQ;
                break;
            case R.id.weixin_share:
                platform = Platform.WEIXIN;
                break;
            case R.id.weibo_share:
                platform = Platform.WEIBO;
                break;
        }
        RxSocial.share(this, platform).setTitle("分享标题").setText("分享的内容").setPageUrl("http://www.qq.com")
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
                platform = Platform.WEIXIN;
                break;
            case R.id.weibo_login:
                platform = Platform.WEIBO;
                break;
        }
        RxSocial.login(this, platform)
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
