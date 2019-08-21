package com.example.rotatepolygon_12_3;

import android.opengl.GLSurfaceView;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 创建一个GLSurfaceView，用于显示OpenGL绘制的图形
        GLSurfaceView glView = new GLSurfaceView(this);
        // 创建GLSurfaceView的内容绘制器
        MyRenderer myRender = new MyRenderer();
        // 为GLSurfaceView设置绘制器
        glView.setRenderer(myRender);
        setContentView(glView);
    }
}
