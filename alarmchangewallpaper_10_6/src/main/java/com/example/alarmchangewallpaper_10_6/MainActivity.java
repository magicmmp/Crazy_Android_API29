package com.example.alarmchangewallpaper_10_6;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity
{
    Button start, stop;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        start = (Button) findViewById(R.id.start);
        stop = (Button) findViewById(R.id.stop);
        // 指定启动ChangeService服务组件
        Intent intent = new Intent(MainActivity.this, ChangeService.class);
        // 创建PendingIntent对象
        final PendingIntent pi = PendingIntent.getService(MainActivity.this,
                0, intent, 0);
        start.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View arg0)
            {
                // 获取AlarmManager对象
                AlarmManager aManager = (AlarmManager) getSystemService(
                        Service.ALARM_SERVICE);
                // 设置每隔5秒执行pi代表的组件一次
                aManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP
                        , 10000, 60000, pi);
                start.setEnabled(false);
                stop.setEnabled(true);
                Toast.makeText(MainActivity.this
                        , "壁纸定时更换启动成功啦",
                        Toast.LENGTH_SHORT).show();
            }
        });
        stop.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View arg0)
            {
                start.setEnabled(true);
                stop.setEnabled(false);
                // 获取AlarmManager对象
                AlarmManager aManager = (AlarmManager) getSystemService(
                        Service.ALARM_SERVICE);
                // 取消对pi的调度
                aManager.cancel(pi);
            }
        });
    }
}
