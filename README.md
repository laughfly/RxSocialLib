# RxSocialLib
整合国内主流社交平台的分享和登录功能，并转换成RxJava数据流
# 特性
* 支持QQ，微信，微博
* 支持RxJava数据流 
* 没有后台取数
# 导入
```groovy
dependencies {
  implementation 'com.laughfly.rxsociallib:rxsocial:+'
}
```
# 使用
## 分享
```java
    RxSocial.share(context, platform)
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
            .setVideoUrl("https://sample-videos.com/video123/mp4/720/big_buck_bunny_720p_2mb.mp4")
            //分享文件
            .setFilePath("本地文件路径")
            //分享微信小程序
            .setMiniProgramPath("微信小程序地址")
            .setMiniProgramType(SocialConstants.MINIPROGRAM_TYPE_RELEASE)
            .setMiniProgramUserName("user")
            
            //分享结果回调
            .start(new SocialCallback<SocialShareResult>() {
               
                @Override
                public void onError(Platform platform, SocialException e) {
                    Toast.makeText(MainActivity.this, "分享失败: " + e.getErrCode(), Toast.LENGTH_SHORT).show();
                }
            
                @Override
                public void onSuccess(Platform platform, SocialShareResult resp) {
                    Toast.makeText(MainActivity.this, "分享成功", Toast.LENGTH_SHORT).show();
                }
            })
            //或者转为Rx数据
            .toObservable()
``` 
## 登录
```java
        RxSocial.login(context, platform)
            .start(new SocialCallback<SocialLoginResult>() {
                @Override
                public void onError(Platform platform, SocialException e) {
                    
                }

                @Override
                public void onSuccess(Platform platform, SocialLoginResult resp) {

                }
            })
            //or
            .toObservable()
```
# 配置
## 平台配置
在res/raw目录下添加json配置文件，比如social_config.json，内容格式如下，把自己的平台配置信息填入相应的json字段。
```json
[
  {
    "platform": "qq",
    "appid": "appid",
    "appsecret": "appsecret",
    "scope": "get_simple_userinfo"
  },
  {
    "platform": "weixin",
    "appid": "appid",
    "appsecret": "appsecret",
    "scope": "snsapi_userinfo",
    "state": "wechat_sdk_live"
  },
  {
    "platform": "weibo",
    "appid": "appid",
    "appsecret": "appsecret",
    "scope": "get_token_info",
    "redirectUrl": "https://api.weibo.com/oauth2/default.html"
  }
]
```
在Application或其他启动初始化的位置添加
```java
   RxSocial.setSocialConfig(context. getResources().openRawResource(R.raw.social_config));
```
## 微信配置
把sample目录里的WXEntryActivity类复制到app目录下，包的路径为`applicationId.wxapi`，并在AndroidManifest.xml添加
```xml
        <!-- 微信分享回调 -->
        <activity
            android:name="applicationId.wxapi.WXEntryActivity"
            android:configChanges="keyboardHidden|orientation|screenSize"
            android:exported="true"
            android:screenOrientation="behind"
            android:theme="@style/Ghost"/>
```
applicationId要替换成自己的应用包名。

## QQ配置
在AndroidManifest.xml添加
```xml
        <activity
            android:name="com.tencent.tauth.AuthActivity"
            android:configChanges="orientation|screenSize|screenLayout|keyboardHidden|navigation"
            android:launchMode="singleTask"
            android:noHistory="true"
            android:screenOrientation="portrait">
            <intent-filter>
                <action android:name="android.intent.action.VIEW"/>

                <category android:name="android.intent.category.DEFAULT"/>
                <category android:name="android.intent.category.BROWSABLE"/>
                <data android:scheme="tencent222222" />
            </intent-filter>
        </activity>
```
并把`tencent222222`的`222222`换成自己的appid。

# TO DO
使用插件简化平台配置