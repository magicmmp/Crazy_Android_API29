package com.example.musicbox_10_7;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

//要处理如果手机里一首歌也没有的情况。否则数组会越界
//要处理如果后台在放歌，界面退出又重新打开的界面显示问题。


/**程序设计思路
 * (1)利用本地广播进行activity和service之间的通信
 * (2)Activity每次重新打开都请求UI数据，更新界面
 * (3)每次播放状态有变化时，Service发UI数据给Activity
 *
 * 已实现的功能：
 * (1)读取手机的MP3歌曲，获取歌名，专辑，专辑封面图片等信息
 * (2)可以控制：播放，暂停，上一首，下一首，停止播放
 * (3)播放界面和后台Service播放状态  实现同步
 *
 * 问题：
 * (1)当手机里一首MP3也没有时，会出现数组下标越界错误。
 */
public class MainActivity extends AppCompatActivity implements OnClickListener
{
    private String TAG="hehe";
    // 获取界面中显示歌曲标题、作者文本框
    private TextView title, author;
    // 播放/暂停、停止按钮
    private ImageButton play, stop,prev,next;
    private ImageView imageView;

    // 定义音乐的播放状态，0x11代表没有播放；0x12代表正在播放；0x13代表暂停
    private int status;//后台播放器的状态码
    Intent startServiceIntent;
    private LocalBroadcastManager localBroadcastManager;
    //Activity只需要接受UI数据的广播
    private ActivityReceiver UI_Update_Receiver;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //需要SD卡访问权限
        if(ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
        }

        Log.d(TAG, "活动onCreate: ");
        setContentView(R.layout.activity_main);
        // 获取程序界面界面中的两个按钮
        play = (ImageButton) this.findViewById(R.id.play);
        stop = (ImageButton) this.findViewById(R.id.stop);
        prev = (ImageButton) this.findViewById(R.id.prev);
        next = (ImageButton) this.findViewById(R.id.next);
        title = (TextView) findViewById(R.id.title);
        author = (TextView) findViewById(R.id.author);
        imageView=findViewById(R.id.image_view);
        // 为两个按钮的单击事件添加监听器
        play.setOnClickListener(this);
        stop.setOnClickListener(this);
        prev.setOnClickListener(this);
        next.setOnClickListener(this);

        localBroadcastManager=LocalBroadcastManager.getInstance(this);
        UI_Update_Receiver = new ActivityReceiver();
        // 创建IntentFilter
        IntentFilter filter = new IntentFilter();
        // 监听service发来的UPDATE_ACTION
        filter.addAction(BroadcastActions.UI_UPDATE_ACTION);
        // 注册BroadcastReceiver，以获取界面更新信息
        localBroadcastManager.registerReceiver(UI_Update_Receiver,filter);

        //启动后台播放服务
        startServiceIntent=new Intent(this,MusicService.class);
        startService(startServiceIntent);
    }

    @Override
    //界面可见时调用
    protected void onStart()
    {
        super.onStart();
        // 发送广播，请求UI更新的数据
        Intent intent = new Intent(BroadcastActions.request_UI_Data_ACTION);
        localBroadcastManager.sendBroadcast(intent);
    }

    // 接收Service发来的本地广播，以更新UI
    public class ActivityReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            // 获取Intent中的update消息，update代表播放状态
            status = intent.getIntExtra("status", 0x11);
            // current代表当前正在播放的歌曲序号
            int current = intent.getIntExtra("current", -1);
            if (status!=0x11)
            {
                Music music=(Music)intent.getSerializableExtra("song");
                StringBuilder songInfo=new StringBuilder();
                int total=intent.getIntExtra("songTotal",-1);

                songInfo.append(current+1+"/"+total+": "+music.name);
                songInfo.append(",歌手："+music.artist);
                songInfo.append(",专辑："+music.album);

                title.setText(songInfo.toString());
                author.setText("时长："+timeParse(music.duration));
                Bitmap bitmap;
                if (music.AlbumImagePath != null)
                {
                    bitmap = BitmapFactory.decodeFile(music.AlbumImagePath);
                }
                else
                {
                    bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ji_guang);
                }
                imageView.setImageBitmap(bitmap);
            }
            switch (status)
            {
                case 0x11:
                    play.setImageResource(R.drawable.play);
                    break;
                // 控制系统进入播放状态
                case 0x12:
                    // 播放状态下设置使用暂停图标
                    play.setImageResource(R.drawable.pause);
                    break;
                // 控制系统进入暂停状态
                case 0x13:
                    // 暂停状态下设置使用播放图标
                    play.setImageResource(R.drawable.play);
                    break;
            }
        }
    }

    /**
     * 将毫秒为单位的音乐时长转换成习惯的时间格式
     * @param duration 音乐时长ms
     * @return 以字符串表示的时间
     */
    private String timeParse(long duration)
    {
        String time = "" ;
        //换成秒为单位
        duration=duration/1000;
        long minute = duration / 60 ;
        long second = duration % 60;
        if( minute < 10 )
        {
            time += "0" ;
        }
        time += minute+":" ;
        if( second < 10 )
        {
            time += "0" ;
        }
        time += second ;
        return time ;
    }

    @Override
    public void onClick(View source)
    {
        // 创建Intent，控制播放器
        Intent intent = new Intent(BroadcastActions.play_CTL_ACTION);
        switch (source.getId())
        {
            // 按下播放/暂停按钮
            case R.id.play:
                intent.putExtra("control", 1);
                break;
            // 按下停止按钮
            case R.id.stop:
                intent.putExtra("control", 2);
                break;
            // 按下播放/暂停按钮
            case R.id.prev:
                intent.putExtra("control", 3);
                break;
            // 按下停止按钮
            case R.id.next:
                intent.putExtra("control", 4);
                break;
        }
        // 发送广播，将被Service组件中的BroadcastReceiver接收到
       localBroadcastManager.sendBroadcast(intent);
    }


    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        Log.d(TAG, "活动onDestroy: ");

        //Activity销毁了，就不需要收听UI数据的广播了
        localBroadcastManager.unregisterReceiver(UI_Update_Receiver);

        //如果后台不放歌，就停止Service
        if(status==0x11)
            stopService(startServiceIntent);
    }

    //处理运行时权限的结果
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {
        switch (requestCode)
        {
            case 1:
                if(grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED)
                {

                }
                else
                {
                    Toast.makeText(this,"你拒绝了读取SD卡的权限",Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;

            default:break;
        }

    }
}
