package com.kwart.tracking;

import android.app.Application;
import android.content.Context;

public class TrackingClass extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        TrackingClass.context = getApplicationContext();
    }


    public static Context getContext() {
        return context;
    }
}
