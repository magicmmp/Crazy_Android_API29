package com.hrw.android.player.service;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;

import com.hrw.android.player.BelmotPlayer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 提供一些查找数据库，搜索目录的方法。
 * 与Android的Service组件无关
 */
public class SystemService
{

	private Context context;
	private Cursor cursor;

	public SystemService(Context context) {
		this.context = context;
	}

	public Cursor getAllSongs() {
		if (cursor != null)
			return cursor;
		ContentResolver resolver = context.getContentResolver();
		cursor = resolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
				null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
		return cursor;

	}

	public String getRealPathByAudioName(String name) {
		Log.i(BelmotPlayer.TAG + "SystemService",
				"getRealPathByAudioName name=" + name);
		ContentResolver resolver = context.getContentResolver();
		String[] proj = { MediaStore.Images.Media.DATA };
		String selection = MediaStore.Audio.Media.DISPLAY_NAME + " = ?";

		String[] selectionArgs = new String[] { name };

		Cursor cursor = resolver.query(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, proj, selection,
				selectionArgs, null);
		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}

	public List<String> getAudioPathList() {
		List<String> list = new ArrayList<String>();
		ContentResolver resolver = context.getContentResolver();
		String[] proj = { MediaStore.Images.Media.DATA };

		Cursor cursor = resolver.query(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, proj, null, null,
				null);

		if (cursor.moveToFirst()) {
			for (int i = 0; i < cursor.getCount(); i++) {
				cursor.moveToPosition(i);
				int column_index = cursor
						.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
				/*
				 * java中\\表示一个\，而regex中\\也表示\，所以当\\\\解析成regex的时候为\\
				 */
				list.add(cursor.getString(column_index));
			}

		}
		return list;

	}

	/**
	 * 搜索音频数据库，将歌曲所在的目录全部返回
	 * @return 目录集合
	 */
	public Set<String> getFolderContainMedia()
	{
		Set<String> f = new HashSet<String>();
		ContentResolver resolver = context.getContentResolver();
		String[] proj = { MediaStore.Images.Media.DATA };
		/**
		 * android本地的媒体信息由MediaStore管理，并通过ContentProvider共享数据。
		 * MediaStore有个内部类MediaStore.Images，维护系统中存储的图片信息，
		 * 通过MediaStore.Images.Media.EXTERNAL_CONTENT_URI可以获取到图片的相关信息，
		 * 包括路径MediaStore.Images.Media.DATA，
		 * 文件名MediaStore.Images.Media.DISPLAY_NAME，
		 * 大小MediaStore.Images.Media.SIZE等信息。
		 */
		//在音乐数据库搜  图片路径MediaStore.Images.Media.DATA?
		Cursor cursor = resolver.query(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, proj, null, null,
				null);
		if (cursor.moveToFirst())
		{
			for (int i = 0; i < cursor.getCount(); i++)
			{
				cursor.moveToPosition(i);
				int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
				/*
				 * java中\\表示一个\，而regex中\\也表示\，所以当\\\\解析成regex的时候为\\
				 */
				f.add(cursor.getString(column_index).substring(0,
						cursor.getString(column_index).lastIndexOf("/") + 1));
			}
		}
		return f;
	}


	/**
	 * 在音频数据库里按路径path查找，返回包含前缀path的所有歌曲
	 * 的路径集合
	 * @param path 路径前缀
	 * @return  所有路径前缀是path的歌曲路径
	 */
	public Set<String> getMediasByFolder(String path)
	{
		Set<String> f = new HashSet<String>();
		ContentResolver resolver = context.getContentResolver();
		//MediaStore.Audio.Media.DATA 歌曲文件的绝对路径
		//MediaStore.Images.Media.DATA也一样
		String[] proj = { MediaStore.Images.Media.DATA };
		// String
		// selection=MediaStore.Audio.Media.DATA+" like '/mnt/sdcard/Recording/%'";
		String selection = MediaStore.Audio.Media.DATA + " like ?";
		String[] selectionArgs = { path + "%" };
		// String selection = MediaStore.Audio.Media.DATA + " like " +"'"
		// +path+"%'";
		Cursor cursor = resolver.query(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, proj, selection,
				selectionArgs, null);
		if (cursor.moveToFirst())
		{
			for (int i = 0; i < cursor.getCount(); i++)
			{
				cursor.moveToPosition(i);
				int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
				/*
				 * java中\\表示一个\，而regex中\\也表示\，所以当\\\\解析成regex的时候为\\
				 */
				f.add(cursor.getString(column_index));
			}
		}
		return f;
	}
}
