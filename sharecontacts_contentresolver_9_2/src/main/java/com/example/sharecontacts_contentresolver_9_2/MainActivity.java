package com.example.sharecontacts_contentresolver_9_2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // 定义该ContentProvider的Authorities
    public static final String AUTHORITY = "MyContacts.Provider";

    // 定义Content所允许操作的三个数据列
    public final static String _ID   = "_id";
    public final static String NAME  = "name";
    public final static String PHONE = "phone";
    // 定义该Content提供服务的两个Uri
    public final static Uri ALL_DATA_URI = Uri
            .parse("content://" + AUTHORITY + "/contacts");
    public final static Uri SINGLE_ITEM_URI = Uri
            .parse("content://"	+ AUTHORITY + "/contact");


    ContentResolver contentResolver;
    private List<Person> contactsList=new ArrayList<>();
    private ListView listView;
    private personAdapter adapter;

    EditText edit_id ;
    EditText edit_name ;
    EditText edit_phone ;

    Button clear_id;
    Button clear_name;
    Button clear_phone;
    Button save_data;
    Button check_data;
    Button delete_item;
    Button delete_all;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        contentResolver=getContentResolver();

        Cursor cursor2=contentResolver.query(ALL_DATA_URI,null,null,
                null,null);
        readCursorAndShow(cursor2,contactsList);

        if(contactsList.isEmpty())
            initContactsList();
        adapter=new personAdapter(this,
                R.layout.person_item,contactsList);
        listView= (ListView)findViewById(R.id.list_view);
        listView.setAdapter(adapter);


        edit_id = (EditText) findViewById(R.id.edit_id);
        edit_name = (EditText) findViewById(R.id.edit_name);
        edit_phone = (EditText) findViewById(R.id.edit_phone);

        clear_id=(Button) findViewById(R.id.clear_id);
        clear_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                edit_id.setText("");
            }
        });
        clear_name=(Button) findViewById(R.id.clear_name);
        clear_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                edit_name.setText("");
            }
        });
        clear_phone=(Button) findViewById(R.id.clear_phone);
        clear_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                edit_phone.setText("");

            }
        });



        save_data=(Button) findViewById(R.id.save_data);
        save_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                String id=edit_id.getText().toString();
                String name=edit_name.getText().toString();
                String phoneNum=edit_phone.getText().toString();

                ContentValues values=new ContentValues();
                values.put(NAME,name);
                values.put(PHONE,phoneNum);

                if(id.equals(""))
                {
                    if(!name.equals("") || !phoneNum.equals(""))
                    {
                        contentResolver.insert(ALL_DATA_URI,values);
                    }
                }
                else
                {
                    Cursor cursor=contentResolver.query(ALL_DATA_URI,new String[]{_ID},
                            "_id = "+id,null,null);
                    //如果id已存在，则更新这一行
                    if(idExisted(cursor))
                    {
                        contentResolver.update(ALL_DATA_URI,values,"_id = "+id,null);
                    }
                    else
                    {
                        values.put(_ID,id);
                        contentResolver.insert(ALL_DATA_URI,values);
                    }
                }
            }
        });

        delete_item=findViewById(R.id.delete_item);
        delete_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                String id=edit_id.getText().toString();
                String name=edit_name.getText().toString();
                String phoneNum=edit_phone.getText().toString();

                if(id.equals(""))
                {
                    contentResolver.delete(ALL_DATA_URI,"name = ? and phone = ?",
                            new String[]{name,phoneNum});
                }
                else
                {
                    Uri newUri=ContentUris.withAppendedId(SINGLE_ITEM_URI,Integer.valueOf(id));
                    contentResolver.delete(newUri,null,null);
                }
            }
        });

        delete_all=findViewById(R.id.delete_all);
        delete_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                contentResolver.delete(ALL_DATA_URI,null,null);
            }
        });



        check_data=(Button) findViewById(R.id.check_data);
        check_data.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Cursor cursor=contentResolver.query(ALL_DATA_URI,null,null,
                        null,null);
                readCursorAndShow(cursor,contactsList);
                adapter.notifyDataSetChanged();
            }
        });
    }

    boolean idExisted(Cursor cursor)
    {
        if(cursor.getCount()==0)
        {
            Log.d("Haha", "cursor.getCount()="+cursor.getCount());
            return  false;
        }
        cursor.moveToFirst();
        Log.d("Haha", "人员编号="+cursor.getLong(cursor.getColumnIndex(_ID)));
        return true;
    }

    void readCursorAndShow(Cursor cursor,List<Person> list)
    {
        //只能改变已和adapter绑定的list，不能重新new一个
        list.clear();
        if(cursor.moveToFirst())
        {
            do {
                Person person=new Person();
                person.setPersonId(cursor.getLong(cursor.getColumnIndex("_id")));
                person.setName(cursor.getString(cursor.getColumnIndex("name")));
                person.setPhone(cursor.getString(cursor.getColumnIndex("phone")));
                list.add(person);
            }while (cursor.moveToNext());
        }
        cursor.close();
    }




    void initContactsList()
    {
        String[] temphones={"135","137","188","136","134","157"};
        String[] tempNames={"刘备","张飞","关羽","曹操","孙权","诸葛亮","吕布"};
        for(int i=0;i<10;i++)
        {
            Person person=new Person();
            person.setPersonId(i);
            person.setName(tempNames[i%tempNames.length]);
            StringBuilder phoneNum=new StringBuilder();
            int phoneIdx=(int)(Math.random()*temphones.length);
            phoneNum.append(temphones[phoneIdx]);
            for(int j=0;j<11-temphones[phoneIdx].length();j++)
            {
                int N=(int)(Math.random()*10);
                phoneNum.append(N);
            }
            person.setPhone(phoneNum.toString());
            contactsList.add(person);
        }
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        // 退出程序时关闭MyDatabaseHelper里的SQLiteDatabase
    }

}
