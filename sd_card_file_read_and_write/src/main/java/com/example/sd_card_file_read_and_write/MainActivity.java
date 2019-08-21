package com.example.sd_card_file_read_and_write;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;

/**
 * 本程序在Android8.0及以下都可正常运行。
 * 在9.0的版本，因为Android修改了文件权限的规则，不能正常运行。
 */

public class MainActivity extends AppCompatActivity {

    EditText edit_write;
    TextView tv_read;
    Button bt_write;
    Button bt_read;
    String FILE_NAME="my_file.txt";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //需要SD卡访问权限
        //android 6.0以下只要在清单声明即可
        if(ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }

        edit_write=findViewById(R.id.edit_write);
        tv_read=findViewById(R.id.tv_read);
        bt_read=findViewById(R.id.bt_read);
        bt_write=findViewById(R.id.bt_write);

        // 为write按钮绑定事件监听器
        bt_write.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View source)
            {
                // 将edit1中的内容写入文件中
                write(edit_write.getText().toString());
            }
        });
        bt_read.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // 读取指定文件中的内容，并显示出来
                tv_read.setText(read());
            }
        });

    }

    private String read()
    {
        try
        {
            // 如果手机插入了SD卡，而且应用程序具有访问SD的权限
            if (Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED))
            {
                // 获取SD卡对应的存储目录
                File sdCardDir = Environment.getExternalStorageDirectory();
                System.out.println("----------------" + sdCardDir);

                File myfile=new File(sdCardDir,FILE_NAME);

                // 获取指定文件对应的输入流
                FileInputStream fis = new FileInputStream(myfile);
                // 将指定输入流包装成BufferedReader
                BufferedReader br = new BufferedReader(new InputStreamReader(fis));

                StringBuilder sb = new StringBuilder("");
                String line = null;
                // 循环读取文件内容
                while ((line = br.readLine()) != null)
                {
                    sb.append(line);
                }
                // 关闭资源
                br.close();
                return sb.toString();
            }
            else
                Log.d("hehe", "手机没插入SD卡");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    private void write(String content)
    {
        try
        {
            // 如果手机插入了SD卡，而且应用程序具有访问SD的权限
            if (Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED))
            {
                // 获取SD卡的目录
                File sdCardDir = Environment.getExternalStorageDirectory();
                File targetFile = new File(sdCardDir,FILE_NAME);

                Log.d("hehe", "文件路径:"+targetFile.getAbsolutePath());
                // 以指定文件创建 RandomAccessFile对象
                RandomAccessFile raf = new RandomAccessFile(
                        targetFile, "rw");
                // 将文件记录指针移动到最后
                raf.seek(targetFile.length());

                // 输出文件内容
                raf.write(content.getBytes());
                // 关闭RandomAccessFile
                raf.close();
            }
            else
                Log.d("hehe", "手机没插入SD卡");
        }
        catch (Exception e)
        {
            e.printStackTrace();
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
                    Toast.makeText(this,"你拒绝了写SD卡的权限",Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;


            default:break;
        }

    }
}
