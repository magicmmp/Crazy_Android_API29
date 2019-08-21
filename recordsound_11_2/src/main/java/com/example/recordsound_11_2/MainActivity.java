package com.example.recordsound_11_2;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener
{
    // 定义界面上的两个按钮
    ImageButton record, stop;
    // 系统的音频文件
    File soundFile;
    MediaRecorder mRecorder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //需要SD卡访问权限
        if(ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
        }



        if(ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.RECORD_AUDIO)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.RECORD_AUDIO},2);
        }
        // 获取程序界面中的两个按钮
        record = (ImageButton) findViewById(R.id.record);
        stop = (ImageButton) findViewById(R.id.stop);
        // 为两个按钮的单击事件绑定监听器
        record.setOnClickListener(this);
        stop.setOnClickListener(this);
    }
    @Override
    public void onDestroy()
    {
        super.onDestroy();
        if (soundFile != null && soundFile.exists())
        {
            // 停止录音
            mRecorder.stop();
            // 释放资源
            mRecorder.release();
            mRecorder = null;
        }
    }
    @Override
    public void onClick(View source)
    {
        switch (source.getId())
        {
            // 单击录音按钮
            case R.id.record:
                if (!Environment.getExternalStorageState().equals(
                        android.os.Environment.MEDIA_MOUNTED))
                {
                    Toast.makeText(MainActivity.this, "SD卡不存在，请插入SD卡！",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                try
                {
                    // 创建保存录音的音频文件
                    soundFile = new File(Environment.getExternalStorageDirectory(),"sound.amr");
                    mRecorder = new MediaRecorder();
                    // 设置录音的声音来源
                    mRecorder.setAudioSource(MediaRecorder
                            .AudioSource.MIC);
                    // 设置录制的声音的输出格式（必须在设置声音编码格式之前设置）
                    mRecorder.setOutputFormat(MediaRecorder
                            .OutputFormat.THREE_GPP);
                    // 设置声音编码的格式
                    mRecorder.setAudioEncoder(MediaRecorder
                            .AudioEncoder.AMR_NB);
                    mRecorder.setOutputFile(soundFile.getAbsolutePath());
                    mRecorder.prepare();
                    // 开始录音
                    mRecorder.start();  // ①
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                break;
            // 单击停止按钮
            case R.id.stop:
                if (soundFile != null && soundFile.exists())
                {
                    // 停止录音
                    mRecorder.stop();  // ②
                    // 释放资源
                    mRecorder.release();  // ③
                    mRecorder = null;
                }
                break;
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

            case 2:
                if(grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED)
                {

                }
                else
                {
                    Toast.makeText(this,"你拒绝了获取音频的权限",Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;


            default:break;
        }

    }
}
