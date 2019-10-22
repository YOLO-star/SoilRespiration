package com.example.soilrespiration.common;

/**
 * 传递消息时使用
 */

public class EventTransfer {

    private String transfer;
    public EventTransfer(String transfer){
        this.transfer = transfer;
    }

    public String getTransfer(){
        return transfer;
    }
}
