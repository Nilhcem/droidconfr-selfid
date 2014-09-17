package com.nilhcem.selfid;

import android.app.Application;

import com.nilhcem.selfid.core.log.ReleaseLogTree;

import timber.log.Timber;

public class SelfidApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        initLogger();
    }

    private void initLogger() {
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            Timber.plant(new ReleaseLogTree());
        }
    }
}
