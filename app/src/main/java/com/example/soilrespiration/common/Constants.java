package com.example.soilrespiration.common;

/**
 * 存放一些常量
 */
public class Constants {
    /*intent tag*/
    public static final String INTENT_IP = "intentIp";  //ip
    public static final String INTENT_PORT = "intentPort";  //port

    /*EventBus msg*/
    public static final String CONNET_SUCCESS = "connectSuccess";

    /*Internet Exception*/
    public static final int HANDLER_HTTP_SEND_FAIL = 1001;
    public static final int HANDLER_HTTP_RECEIVE_FAIL = 1002;
}
