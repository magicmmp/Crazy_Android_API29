package com.example.videoview_11_1;

import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Environment;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    VideoView videoView;
    MediaController mController;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        setContentView(R.layout.activity_main);
        // 获取界面上VideoView组件
        videoView = (VideoView) findViewById(R.id.video);
        // 创建MediaController对象
        mController = new MediaController(this);

        File video = new File(Environment.getExternalStorageDirectory(),"/DCIM/Camera/VID_20190815_031038.mp4");
        if(video.exists())
        {
            videoView.setVideoPath(video.getAbsolutePath()) ;  // ①
            // 设置videoView与mController建立关联
            videoView.setMediaController(mController);  // ②
            // 设置mController与videoView建立关联
            mController.setMediaPlayer(videoView);  // ③
            // 让VideoView获取焦点
            videoView.requestFocus();
        }
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
