package com.example.musicbox_10_7;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MusicService extends Service
{
    private LocalBroadcastManager localBroadcastManager;
    MyReceiver serviceReceiver;
    List<Music> songList=new ArrayList<>();
    MediaPlayer mPlayer;
    String TAG="hehe";
    // 当前的状态，0x11代表没有播放；0x12代表正在播放；0x13代表暂停
    private int status = Constants.IDLE;
    // 记录当前正在播放的音乐
    private int current = 0;
    private int songTotal=0;
    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }
    @Override
    public void onCreate()
    {
        Log.d(TAG, "服务create。");
        super.onCreate();
        //访问音频数据库，获得歌曲列表
        getLocalMusic(songList);

        songTotal=songList.size();

        localBroadcastManager=LocalBroadcastManager.getInstance(this);
        // 创建BroadcastReceiver
        serviceReceiver = new MyReceiver();
        // 创建IntentFilter
        IntentFilter filter = new IntentFilter();
        //接受播放控制和UI数据更新 两种广播请求
        filter.addAction(Constants.play_CTL_ACTION);
        filter.addAction(Constants.request_UI_Data_ACTION);
        localBroadcastManager.registerReceiver(serviceReceiver, filter);
        // 创建MediaPlayer
        mPlayer = new MediaPlayer();
        // 为MediaPlayer播放完成事件绑定监听器
        mPlayer.setOnCompletionListener(new OnCompletionListener()
        {
            @Override
            public void onCompletion(MediaPlayer mp)
            {
                current++;
                if (current >= songList.size())
                {
                    current = 0;
                }

                // 准备并播放音乐
                prepareAndPlay(songList.get(current).path);
                //发送UI更新信息
                SendUIData();
            }
        });

        /**
         * android8.0及以上需要设置通知渠道NotificationChannel。
         * 否则，通知将无法展示。
         */
        String CHANNEL_ONE_ID = "com.primedu.cn";
        String CHANNEL_ONE_NAME = "Channel One";
        NotificationChannel notificationChannel = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notificationChannel = new NotificationChannel(CHANNEL_ONE_ID,
                    CHANNEL_ONE_NAME, NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setShowBadge(true);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.createNotificationChannel(notificationChannel);
        }
        //设为前台服务，避免Service被回收
        Intent intent=new Intent(this,MainActivity.class);
        PendingIntent pi=PendingIntent.getActivity(this,0,intent,0);
        Notification notification=new NotificationCompat.Builder(this)
                .setChannelId(CHANNEL_ONE_ID)
                .setContentTitle("音乐盒")
                .setContentText("音乐盒正在运行")
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.icon)
                .setContentIntent(pi)
                .build();

        startForeground(1,notification);
    }




    /**
     * 当播放状态改变时，发送Activity需要的所有数据，
     * 以更新播放界面
     */
    void SendUIData()
    {
        // 广播通知Activity更改图标、文本框
        Intent sendIntent = new Intent(Constants.UI_UPDATE_ACTION);
        sendIntent.putExtra("songTotal", songTotal);
        sendIntent.putExtra("current", current);
        sendIntent.putExtra("status", status);
        sendIntent.putExtra("song",songList.get(current));
        // 发送广播
        localBroadcastManager.sendBroadcast(sendIntent);
    }


    /**
     * 接收Activity发来的广播消息
     */
    public class MyReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(final Context context, Intent intent)
        {
            //如果是请求UI数据的
            if(intent.getAction()== Constants.request_UI_Data_ACTION)
            {
                SendUIData();
                return;
            }

            //如果是控制播放的
            int control = intent.getIntExtra("control", -1);
            switch (control)
            {
                // 播放或暂停
                case 1:
                    // 原来处于没有播放状态
                    if (status == Constants.IDLE)
                    {
                        // 准备并播放音乐
                        prepareAndPlay(songList.get(current).path);
                        status = Constants.PLAYING;
                    }
                    // 原来处于播放状态
                    else if (status == Constants.PLAYING)
                    {
                        // 暂停
                        mPlayer.pause();
                        // 改变为暂停状态
                        status = Constants.PAUSE;
                    }
                    // 原来处于暂停状态
                    else if (status == Constants.PAUSE)
                    {
                        // 播放
                        mPlayer.start();
                        // 改变状态
                        status = Constants.PLAYING;
                    }
                    break;
                // 停止播放
                case 2:
                    // 如果原来正在播放或暂停
                    if (status == Constants.PLAYING || status == Constants.PAUSE)
                    {
                        // 停止播放
                        mPlayer.stop();
                        status = Constants.IDLE;
                    }
                    break;
                    //上一首
                case 3:
                    if (status == Constants.PLAYING || status == Constants.PAUSE)
                    {
                        // 先停止当前歌曲播放
                        mPlayer.stop();
                    }
                    current--;
                    if(current<0)
                        current=songList.size()-1;
                    // 准备并播放音乐
                    prepareAndPlay(songList.get(current).path);
                    status = Constants.PLAYING;
                    break;
                    //下一首
                case 4:
                    // 如果原来正在播放或暂停
                    if (status == Constants.PLAYING || status == Constants.PAUSE)
                    {
                        // 停止播放
                        mPlayer.stop();
                    }
                    current++;
                    if(current>=songList.size())
                        current=0;

                    // 准备并播放音乐
                    prepareAndPlay(songList.get(current).path);
                    status = Constants.PLAYING;

                    break;
            }
           SendUIData();
        }
    }
    private void prepareAndPlay(String path)
    {
        try
        {
            mPlayer.reset();
            // 使用MediaPlayer加载指定的声音文件。
            mPlayer.setDataSource(path);
            // 准备声音
            mPlayer.prepare();
            // 播放
            mPlayer.start();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }



    void getLocalMusic(List<Music> musicList)
    {
        musicList.clear();
        ContentResolver resolver = this.getContentResolver();
        String selection = MediaStore.Audio.Media.DATA+" like ?";
        String[] selectionArgs = { "_"+"%.mp3"  };
        Cursor cursor = resolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                               null, selection, selectionArgs, null);
        cursor.moveToFirst();
        if(cursor.getCount()>0)
            do {
                Music m = new Music();
                m.name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                m.artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                m.album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                m.path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                m.duration = Long.parseLong(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)));
                //获取专辑ID
                int albumId = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
                //根据专辑ID获取到专辑封面图
                m.AlbumImagePath = getAlbumImagePath(albumId);

                musicList.add(m);
            } while (cursor.moveToNext());
        cursor.close();
    }

    /**
     * 根据专辑ID获取专辑封面图
     * @param album_id 专辑ID
     * @return 封面图片文件路径
     */
    private String getAlbumImagePath(long album_id)
    {
        String mUriAlbums = "content://media/external/audio/albums";
        String[] projection = new String[]{"album_art"};
        Cursor cur = this.getContentResolver().query(Uri.parse(mUriAlbums + "/" + Long.toString(album_id)), projection, null, null, null);
        String path = null;
        if (cur.getCount() > 0 && cur.getColumnCount() > 0)
        {
            cur.moveToNext();
            path= cur.getString(0);
        }
        cur.close();

        return path;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        Log.d(TAG, "服务destroy。");
        //取消通知栏前台显示
        stopForeground(true);
        mPlayer.release();
        localBroadcastManager.unregisterReceiver(serviceReceiver);
    }
}
