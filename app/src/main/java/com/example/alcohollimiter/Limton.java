package com.example.alcohollimiter;

import android.app.Application;
// 임시 싱글톤 이름
public class Limton extends Application {
    public static RealtimeData realtimeData;
    public void onCreate() {
        super.onCreate();
        realtimeData = new RealtimeData();
    }
    public static RealtimeData getRealtimeData(){
        return realtimeData;
    }

}
