package com.example.musicbox_10_7;


import java.io.Serializable;

/**
 * 描述一首歌所有信息的JavaBean
 */

public class Music implements Serializable
{
	String name;
	String artist;
	String album;
	String path;
	String AlbumImagePath;
	long duration;
//	Bitmap thumbBitmap;
}
