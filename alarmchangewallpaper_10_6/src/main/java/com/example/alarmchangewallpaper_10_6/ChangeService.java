package com.example.alarmchangewallpaper_10_6;

import android.app.Service;
import android.app.WallpaperManager;
import android.content.Intent;
import android.os.IBinder;

public class ChangeService extends Service {

    // 定义定时更换的壁纸资源
    int[] wallpapers = new int[]{
            R.drawable.last,
            R.drawable.p1,
            R.drawable.p2,
            R.drawable.p3,
            R.drawable.p10
    };
    // 定义系统的壁纸管理服务
    WallpaperManager wManager;
    // 定义当前所显示的壁纸
    int current = 0;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        // 如果到了最后一张，系统重新开始
        if(current >= wallpapers.length)
            current = 0;
        try
        {
            // 改变壁纸
            wManager.setResource(wallpapers[current++]);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return START_STICKY;
    }
    @Override
    public void onCreate()
    {
        super.onCreate();
        // 初始化WallpaperManager
        wManager = WallpaperManager.getInstance(this);
    }
    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }
}
