package com.example.sharecontacts_contentprovider_9_2;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.content.ContentUris;
import android.net.Uri;
import android.content.UriMatcher;
import android.database.sqlite.SQLiteDatabase;




public class MyContentProvider extends ContentProvider
{

    // 定义该ContentProvider的Authorities
    public static final String AUTHORITY = "MyContacts.Provider";

    public final static String TABLE_NAME   = "contacts";
    public final static String DB_NAME   = "myDatabase.db3";
    // 定义Content所允许操作的三个数据列
    public final static String _ID   = "_id";
    public final static String NAME  = "name";
    public final static String PHONE = "phone";
    // 定义该Content提供服务的两个Uri
    public final static Uri ALL_DATA_URI = Uri
            .parse("content://" + AUTHORITY + "/contacts");
    public final static Uri SINGLE_ITEM_URI = Uri
            .parse("content://"	+ AUTHORITY + "/contact");

    private static UriMatcher matcher
            = new UriMatcher(UriMatcher.NO_MATCH);
    private static final int ALL = 1;
    private static final int SINGLE = 2;
    private MyDatabaseHelper dbOpenHelper;

    static
    {
        // 为UriMatcher注册两个Uri
        matcher.addURI(AUTHORITY, "contacts", ALL);
        matcher.addURI(AUTHORITY, "contact/#", SINGLE);
    }


    public MyContentProvider() {

    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs)
    {
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        // 记录所删除的记录数
        int num = 0;
        // 对uri进行匹配
        switch (matcher.match(uri))
        {
            // 如果Uri参数代表操作全部数据项
            case ALL:
                num = db.delete(TABLE_NAME, selection, selectionArgs);
                if(selection==null && selectionArgs==null)
                    //要想将所有表的自增列都归零，直接清空sqlite_sequence表就可以了：
                    db.execSQL("DELETE FROM sqlite_sequence");//经测试只有这句可行
                break;
            // 如果Uri参数代表操作指定数据项
            case SINGLE:
                // 解析出所需要删除的记录ID
                long id = ContentUris.parseId(uri);
                String whereClause = _ID + "=" + id;
                // 如果原来的where子句存在，拼接where子句
                if (selection != null && !selection.equals(""))
                {
                    whereClause = whereClause + " and " + selection;
                }
                num = db.delete(TABLE_NAME, whereClause, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("未知Uri:" + uri);
        }
        // 通知数据已经改变
        getContext().getContentResolver().notifyChange(uri, null);
        return num;
    }

    @Override
    public String getType(Uri uri) {
        switch (matcher.match(uri))
        {
            // 如果操作的数据是多项记录
            case ALL:
                return "vnd.android.cursor.dir/mycontacts";
            // 如果操作的数据是单项记录
            case SINGLE:
                return "vnd.android.cursor.item/mycontacts";
            default:
                throw new IllegalArgumentException("未知Uri:" + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // 获得数据库实例
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        switch (matcher.match(uri))
        {
            // 如果Uri参数代表操作全部数据项
            case ALL:
                // 插入数据，返回插入记录的ID
                long rowId = db.insert(TABLE_NAME, _ID, values);
                // 如果插入成功返回uri
                if (rowId > 0)
                {
                    // 在已有的 Uri的后面追加ID
                    Uri wordUri = ContentUris.withAppendedId(uri, rowId);
                    // 通知数据已经改变
                    getContext().getContentResolver()
                            .notifyChange(wordUri, null);
                    return wordUri;
                }
                break;
            default :
                throw new IllegalArgumentException("未知Uri:" + uri);
        }
        return null;
    }

    @Override
    public boolean onCreate() {
        dbOpenHelper = new MyDatabaseHelper(this.getContext(),
                DB_NAME, 1);
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder)
    {
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        switch (matcher.match(uri))
        {
            // 如果Uri参数代表操作全部数据项
            case ALL:
                // 执行查询
                return db.query(TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
            // 如果Uri参数代表操作指定数据项
            case SINGLE:
                // 解析出想查询的记录ID
                long id = ContentUris.parseId(uri);
                String whereClause = _ID + "=" + id;
                // 如果原来的where子句存在，拼接where子句
                if (selection != null && !"".equals(selection))
                {
                    whereClause = whereClause + " and " + selection;
                }
                return db.query(TABLE_NAME, projection, whereClause, selectionArgs,
                        null, null, sortOrder);
            default:
                throw new IllegalArgumentException("未知Uri:" + uri);
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs)
    {
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        // 记录所修改的记录数
        int num = 0;
        switch (matcher.match(uri))
        {
            // 如果Uri参数代表操作全部数据项
            case ALL:
                num = db.update(TABLE_NAME, values, selection, selectionArgs);
                break;
            // 如果Uri参数代表操作指定数据项
            case SINGLE:
                // 解析出想修改的记录ID
                long id = ContentUris.parseId(uri);
                String whereClause = _ID + "=" + id;
                // 如果原来的where子句存在，拼接where子句
                if (selection != null && !selection.equals(""))
                {
                    whereClause = whereClause + " and " + selection;
                }
                num = db.update(TABLE_NAME, values, whereClause, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("未知Uri:" + uri);
        }
        // 通知数据已经改变
        getContext().getContentResolver().notifyChange(uri, null);
        return num;
    }
}
