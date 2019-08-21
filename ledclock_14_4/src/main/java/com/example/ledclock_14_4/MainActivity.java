package com.example.ledclock_14_4;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SimpleDateFormat df = new SimpleDateFormat(
                "HHmmss");
        // 将当前时间格式化成HHmmss的形式
        String timeStr = df.format(new Date());
        Log.d("hehe", "简化时间格式："+timeStr);
    }
}
