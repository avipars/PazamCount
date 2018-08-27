package com.aviparshan.pazamcount;

import android.app.Application;

import com.onesignal.OneSignal;

/**
 * Created by avipars on 8/27/2018 on com.aviparshan.pazamcount
 */
public class ApplicationClass extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // OneSignal Initialization
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();
    }
}
