package com.example.listactivity_listener_test;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends ListActivity
{
    String[] arr = { "孙悟空", "猪八戒", "唐僧" };
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.activity_main);
        // 无须使用布局文件

        // 创建ArrayAdapter对象
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_multiple_choice, arr);
        // 设置该窗口显示列表
        setListAdapter(adapter);

        ListView listView=this.getListView();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id)
            {
                Toast.makeText(getApplicationContext(),"点中了"+arr[position],
                        Toast.LENGTH_LONG).show();
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id)
            {
                Toast.makeText(getApplicationContext(),"长按了"+arr[position],
                        Toast.LENGTH_LONG).show();
                return false;
            }
        });


    }
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id)
    {
        super.onListItemClick(l, v, position, id);
        Toast.makeText(getApplicationContext(),"点中了猪头"+arr[position],Toast.LENGTH_LONG).show();
    }


}
