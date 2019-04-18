# RxSocialLib
* 支持QQ，QQZone，微信，微信朋友圈，微博，微博故事
* 兼容RxJava数据流 
* 没有后台取数
# 导入
在根目录下的build.gradle文件添加
```groovy
buildscript {
    dependencies {
        classpath 'com.laughfly.rxsociallib:plugin:0.3.2.1'
    }
}
```
# 使用
在全局初始化的位置调用
```java
    RxSocial.initialize(context);
```
## 分享
```java
    RxSocial.share(context)
            //SharePlatform根据配置文件自动生成
            .setPlatform(SharePlatform.Wechat)
            .setTitle("标题")
            .setText("内容")
            .setThumbUri("图标地址")
            //分享网页
            .setPageUrl("https://sample-videos.com")
            //分享图片
            .setImageUri("https://sample-videos.com/img/Sample-jpg-image-500kb.jpg")
            //分享音频
            .setAudioUri("https://sample-videos.com/audio/mp3/wave.mp3")
            //分享视频
            .setVideoUri("https://sample-videos.com/video123/mp4/720/big_buck_bunny_720p_2mb.mp4")
            //分享文件
            .setFileUri("本地文件路径")
            //分享微信小程序
            .setMiniProgramPath("微信小程序地址")
            .setMiniProgramType(SocialConstants.MINIPROGRAM_TYPE_RELEASE)
            .setMiniProgramUserName("user")
            
            //分享结果回调
            .start(new SocialCallback<SocialShareResult>() {
               
                @Override
                public void onError(String platform, SocialException e) {
                    Toast.makeText(MainActivity.this, "分享失败: " + e.getErrCode(), Toast.LENGTH_SHORT).show();
                }
            
                @Override
                public void onSuccess(String platform, SocialShareResult resp) {
                    Toast.makeText(MainActivity.this, "分享成功", Toast.LENGTH_SHORT).show();
                }
            })
            //或者转为Rx数据
            .toObservable()
``` 
## 登录
```java
        RxSocial.login(context)
            .setPlatform(LoginPlatform.Wechat)
            .start(new SocialCallback<SocialLoginResult>() {
                @Override
                public void onError(String platform, SocialException e) {
                    Toast.makeText(MainActivity.this, "登录失败: " + e.getErrCode(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(String platform, SocialLoginResult resp) {
                    Toast.makeText(MainActivity.this, "登录成功\nuid: " + socialLoginResult.uid, Toast.LENGTH_SHORT).show();
                }
            })
            //or
            .toObservable()
```
# 配置

参照sample下的social-config.gradle文件进行平台配置，只有写在配置里的平台的依赖包会被导入。
```groovy
apply plugin: 'social-config'

RxSocialConfig{
    Weibo {
        appId 'yourAppId'
        appSecret 'yourAppSecret'
        redirectUrl 'https://api.weibo.com/oauth2/default.html'
        scope 'get_token_info'
    }
    WeiboStory {
        appId 'yourAppId'
        appSecret 'yourAppSecret'
        redirectUrl 'https://api.weibo.com/oauth2/default.html'
        scope 'get_token_info'
        login false
    }
    QQ {
        appId 'yourAppId'
        appSecret 'yourAppSecret'
        scope 'get_simple_userinfo'
    }
    QQZone {
        appId 'yourAppId'
        appSecret 'yourAppSecret'
        scope 'get_simple_userinfo'
        login false
    }
    Wechat {
        appId 'yourAppId'
        appSecret 'yourAppSecret'
        scope 'snsapi_userinfo'
        state 'wechat_sdk_live'
    }
    WechatMoments {
        appId 'yourAppId'
        appSecret 'yourAppSecret'
        scope 'snsapi_userinfo'
        state 'wechat_sdk_live'
        login false
    }
}
```
然后在你的app的build.gradle里添加
```groovy
apply from:'social-config.gradle'
```
**刷新，完成！**

## 其他  
修改social-config.gradle里的平台信息后可能需要Rebuild Project才有效果！

# TO DO
~~使用插件简化平台配置~~  
~~分离平台实现代码~~  
添加更多平台