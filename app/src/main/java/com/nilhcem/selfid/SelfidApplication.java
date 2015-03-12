package com.nilhcem.selfid;

import android.app.Application;

import timber.log.Timber;

public class SelfidApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }
}
