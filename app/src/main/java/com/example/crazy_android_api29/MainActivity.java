package com.example.crazy_android_api29;

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StringBuilder stringBuilder=new StringBuilder();
        ContentResolver resolver = getContentResolver();
        String[] proj = { MediaStore.Images.Media.DATA };
        // String
        // selection=MediaStore.Audio.Media.DATA+" like '/mnt/sdcard/Recording/%'";
      //  String selection = MediaStore.Audio.Media.DATA + " like ?";
      //  String[] selectionArgs = { path + "%" };
        // String selection = MediaStore.Audio.Media.DATA + " like " +"'"
        // +path+"%'";
        Cursor cursor = resolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null,
                null, null);
        if (cursor.moveToFirst())
        {
            for (int i = 0; i < cursor.getCount(); i++)
            {
                cursor.moveToPosition(i);
                stringBuilder.append("MediaStore.Audio.Media.DATA\n");
                stringBuilder.append(cursor.getString(cursor
                        .getColumnIndexOrThrow(MediaStore.Audio.Media.DATA))+"\n");

                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                /*
                 * java中\\表示一个\，而regex中\\也表示\，所以当\\\\解析成regex的时候为\\
                 */
                stringBuilder.append("MediaStore.Images.Media.DATA\n");
                stringBuilder.append(cursor.getString(column_index)+"\n\n");
            }
        }
        TextView textView=findViewById(R.id.text_view);
        textView.setText(stringBuilder.toString());

    }
}
