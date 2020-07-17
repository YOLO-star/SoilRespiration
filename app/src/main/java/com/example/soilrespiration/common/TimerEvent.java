package com.example.soilrespiration.common;

public class TimerEvent {

    private long timer;

    public TimerEvent(long time){
        timer = time;
    }

    public long getTimer(){
        return timer;
    }
}
