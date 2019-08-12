package com.example.sharecontacts_contentprovider_9_2;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDatabaseHelper extends SQLiteOpenHelper
{

    public MyDatabaseHelper(Context context, String name, int version)
    {
        super(context, name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase)
    {
        String CREATE_TABLE_SQL =
                "create table contacts(_id integer primary " +
                        "key autoincrement , name text, phone text)";
        // 第一次使用数据库时自动建表
        sqLiteDatabase.execSQL(CREATE_TABLE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1)
    {
        System.out.println("--------onUpdate Called--------"
                + i + "--->" + i1);
    }
}












