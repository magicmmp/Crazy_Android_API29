package com.example.musicbox_10_7;

/**
 * 常量类。
 * 本地广播Action常量：用于Activity和Service之间通信
 * 播放器状态常量：表示播放、暂停、停止等状态
 */
public class Constants
{
    //控制播放器的Action。例如控制播放或暂停
    public static final String play_CTL_ACTION = "PLAY_CTL_ACTION";
    //向Service请求UI数据的Action
    public static final String request_UI_Data_ACTION = "request_UI_Data_ACTION";
    //Service发送的UI数据Action
    public static final String UI_UPDATE_ACTION = "UI_UPDATE_ACTION";
    public static final int IDLE=0;
    public static final int PLAYING=1;
    public static final int PAUSE=2;

}
