package com.laughfly.rxsociallib.sample;

import android.app.Application;

import com.laughfly.rxsociallib.RxSocial;
import com.squareup.leakcanary.LeakCanary;

/**
 * Created by caowy on 2019/4/24.
 * email:cwy.fly2@gmail.com
 */

public class SampleApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
        RxSocial.initialize(this);
        RxSocial.setLogEnable(BuildConfig.DEBUG);
    }
}
