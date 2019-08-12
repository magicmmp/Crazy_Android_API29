package com.example.sharecontacts_contentprovider_9_2;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
{
    MyDatabaseHelper dbHelper;
    SQLiteDatabase db;
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



        dbHelper = new MyDatabaseHelper(this, "myDatabase.db3", 1);
        db=dbHelper.getWritableDatabase();
        Cursor cursor2=db.rawQuery("select * from contacts",null);
        readCursorAndShow(cursor2,contactsList);

        //初始化联系人数组，在没有数据插入的时候也能有显示
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
                db=dbHelper.getWritableDatabase();
                if(id.equals(""))
                {
                    if(!name.equals("") || !phoneNum.equals(""))
                        db.execSQL("insert into contacts(name,phone) values(?,?)"
                            , new String[] {name, phoneNum});
                }
                else
                {
                    Cursor cursor=db.rawQuery("select count(*) as counts from contacts"
                            + " where _id=?",new String[]{id});
                    //如果id已存在，则更新这一行
                    if(idExisted(cursor))
                    {
                        ContentValues values=new ContentValues();
                        values.put("name",name);
                        values.put("phone",phoneNum);
                        db.update("contacts",values,"_id = ?",new String[]{id});
                    }
                    else
                    {
                        db.execSQL("insert into contacts(_id,name,phone) values(?,?,?)"
                                , new String[] {id,name, phoneNum});
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
                db=dbHelper.getWritableDatabase();
                if(id.equals(""))
                {
                    db.delete("contacts","name = ? and phone = ?",
                            new String[]{name,phoneNum});
                }
                else
                {
                    db.delete("contacts","_id = ?",
                            new String[]{id});
                }
            }
        });

        delete_all=findViewById(R.id.delete_all);
        delete_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                db.delete("contacts",null,null);
                db.execSQL("DELETE FROM sqlite_sequence");
            }
        });



        check_data=(Button) findViewById(R.id.check_data);
        check_data.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                db=dbHelper.getWritableDatabase();
                Cursor cursor=db.rawQuery("select * from contacts",null);
                readCursorAndShow(cursor,contactsList);
                adapter.notifyDataSetChanged();
            }
        });
    }

    boolean idExisted(Cursor cursor)
    {
        if(cursor.moveToFirst())
        {
            long count=cursor.getLong(cursor.getColumnIndex("counts"));
            cursor.close();
            if(count>0)
            {
                return true;
            }
        }
        return false;
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
        for(int i=0;i<100;i++)
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
            db.execSQL("insert into contacts(_id,name,phone) values(?,?,?)"
                    , new String[] {person.getPersonId()+"",person.getName(), person.getPhone()});
        }
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        // 退出程序时关闭MyDatabaseHelper里的SQLiteDatabase
        if (dbHelper != null)
        {
            dbHelper.close();
        }
    }



}
