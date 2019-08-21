package com.example.minibrowser_13_4;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    EditText url;
    Button go;
    WebView show;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 获取页面中文本框、WebView组件
        url = (EditText) findViewById(R.id.url);
        go=findViewById(R.id.go);
        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String urlStr = url.getText().toString();
                // 加载、并显示urlStr对应的网页
                show.loadUrl(urlStr);
            }
        });
        show = (WebView) findViewById(R.id.show);
    }

}
