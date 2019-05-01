package com.laughfly.rxsociallib.delegate;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by caowy on 2019/4/20.
 * email:cwy.fly2@gmail.com
 */

public class DelegateHelper {
    public static void startActivity(Context context, final Class clazz, DelegateCallback delegateCallback, final ResultCallback resultCallback) {
        final Application application = (Application) context.getApplicationContext();
        ALC alc = new ALC(){
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                if(activity.getClass() == clazz) {
                    SocialDelegateActivity delegateActivity = (SocialDelegateActivity) activity;
                    delegateActivity.setDelegateCallback(delegateCallback);
                    delegateActivity.setResultCallback(resultCallback);
                    application.unregisterActivityLifecycleCallbacks(this);
                }
            }
        };
        try {
            application.registerActivityLifecycleCallbacks(alc);
            Intent intent = new Intent(application, clazz);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            application.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            application.unregisterActivityLifecycleCallbacks(alc);
        }
    }

    private abstract static class ALC implements Application.ActivityLifecycleCallbacks {

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

        }

        @Override
        public void onActivityStarted(Activity activity) {

        }

        @Override
        public void onActivityResumed(Activity activity) {

        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {

        }
    }
}
