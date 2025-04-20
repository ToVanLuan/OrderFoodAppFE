package com.example.orderfoodonline;

import android.app.Application;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Khởi tạo Facebook SDK (chỉ cần fullyInitialize)
        FacebookSdk.sdkInitialize(getApplicationContext());
        FacebookSdk.fullyInitialize();
        FacebookSdk.setAutoLogAppEventsEnabled(true);
        AppEventsLogger.activateApp(this);
    }
}