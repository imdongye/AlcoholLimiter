package com.example.alcohollimiter;

import java.util.Calendar;

public class RealtimeData {
    Calendar calendar;
    long startTime;

    public RealtimeData() {
        calendar = Calendar.getInstance();
    }
    public void setStartTime(){
        startTime = calendar.getTimeInMillis();
    }
    public String getElapsedTime() {
        String rtn = "a";
        return rtn;
    }
}
