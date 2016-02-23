package com.ddhigh.study.todolist;

import android.app.Application;
import android.content.res.Configuration;
import android.util.Log;

/**
 *
 */
public class MyApplication extends Application {
    private static MyApplication instance;
    public final static String TAG = "xialei===>";

    public static MyApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        Log.d(TAG, "onCreate");
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.d(TAG, "onLowMemory");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.d(TAG, "onConfigurationChanged");
    }
}
