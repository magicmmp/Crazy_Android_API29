package com.hrw.android.player.dao.impl;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.net.Uri;

import com.hrw.android.player.builder.ContentValuesBuilder;
import com.hrw.android.player.dao.PlaylistDao;
import com.hrw.android.player.db.constants.UriConstant;
import com.hrw.android.player.domain.Playlist;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PlaylistDaoImpl extends ContextWrapper implements PlaylistDao {
	private ContentResolver cr;
	private Uri uri = Uri.parse(UriConstant.PLAYLIST_URI);
	private ContentValues values = new ContentValues();

	public PlaylistDaoImpl(Context base)
	{
		super(base);
	}

	//新建名为name的播放列表
	@Override
	public void createPlaylist(String name)
	{
		cr = getContentResolver();
		Playlist pl = new Playlist();
		pl.setName(name);
		pl.setAddDate(new Date());
		pl.setUpdateDate(new Date());
		try
		{
			values = ContentValuesBuilder.getInstance().bulid(pl);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		cr.insert(uri, values);
	}

	@Override
	public List<Playlist> getAllPlaylist()
	{
		cr = getContentResolver();
		List<Playlist> playlist = new ArrayList<Playlist>();
		String[] projection = { "id", "name" };
		Cursor c = cr.query(uri, projection, null, null, null);
		if (c.moveToFirst())
		{
			for (int i = 0; i < c.getCount(); i++)
			{
				Playlist p = new Playlist();
				c.moveToPosition(i);
				p.setId(c.getLong(0));
				p.setName(c.getString(1));
				playlist.add(p);
			}
		}
		c.close();
		return playlist;
	}

	@Override
	//这是有问题的
	//删了某播放列表，但是属于该列表的歌曲并没有从Audio数据库表格中删除
	//要从新设计数据库的表格结构
	public void removePlaylist(String id)
	{
		cr = getContentResolver();
		// cr.query(uri, null, "id = ?", new String[] { id }, null);
		cr.delete(uri, "id = ?", new String[] { id });
	}

}
