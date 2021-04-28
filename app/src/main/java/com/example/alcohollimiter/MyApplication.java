package com.example.alcohollimiter;

import android.app.Application;

public class MyApplication extends Application {
    public static RealtimeData realtimeData;
    public void onCreate() {
        super.onCreate();
        realtimeData = new RealtimeData();
    }
    public static RealtimeData getRealtimeData(){
        return realtimeData;
    }

}
