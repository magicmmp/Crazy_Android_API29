package com.example.musicbox_10_7;

/**
 * 本地广播Action常量，
 * 用于Activity和Service之间通信
 */
public class BroadcastActions
{
    //控制播放器的Action。例如控制播放或暂停
    public static final String play_CTL_ACTION = "PLAY_CTL_ACTION";
    //向Service请求UI数据的Action
    public static final String request_UI_Data_ACTION = "request_UI_Data_ACTION";
    //Service发送的UI数据Action
    public static final String UI_UPDATE_ACTION = "UI_UPDATE_ACTION";
}
