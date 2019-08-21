package com.hrw.android.player.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hrw.android.player.R;
import com.hrw.android.player.adapter.MusicListAdapter;
import com.hrw.android.player.component.dialog.CommonAlertDialogBuilder;
import com.hrw.android.player.dao.AudioDao;
import com.hrw.android.player.dao.impl.AudioDaoImpl;
import com.hrw.android.player.db.constants.UriConstant;
import com.hrw.android.player.domain.Audio;
import com.hrw.android.player.service.SystemService;
import com.hrw.android.player.utils.Constants;
import com.hrw.android.player.utils.Constants.PopupMenu;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 这是播放列表里面，对现有自定义播放列表添加歌曲的界面
 * 点击"播放列表"里面的某个列表名，就会打开这个界面
 * 应该改进的有：
 * 改进1：添加歌曲应该是在现有本地播放列表里选择，因为所有本地音乐都在里面
 * 改进2：无论点上面工具栏或者手机的返回键，都应该返回上次的界面，
 *       而不是直接返回"曲库"
 *
 */
public class MusicListActivity extends BaseListActivity
{
	private AudioDao audioDao = new AudioDaoImpl(this);
	private ImageButton back_btn;
	private ProgressDialog progress_dialog;
	private List<Audio> musicList;
	private List<Integer> checkedItem = new ArrayList<>();//choices的下标，表示选中的目录
	private String[] choices;//所有包含音频文件的目录
	private MusicListAdapter adapter;
	Set<Integer> popUpMenu = new HashSet<Integer>();
	private Handler musicListHandler = new Handler()
    {
		public void handleMessage(Message msg)
        {
			switch (msg.what)
            {
                case 0:
                {
                    progress_dialog.dismiss();
                    break;
                }
                default:
                    break;
			}
		}
	};


	@Override
	//系统Activity的对话框，在弹出之前做一些设置
	protected void onPrepareDialog(int id, Dialog dialog)
    {
		AlertDialog alertDialog = (AlertDialog) dialog;
		ListView lv = alertDialog.getListView();
		for (int i = 0; i < choices.length; i++)
		{
			lv.setItemChecked(i, false);
		}
		super.onPrepareDialog(id, dialog);
	}

	protected void showProcessDialog()
	{
		progress_dialog = ProgressDialog.show(this, null, "正在扫描");
	}

	@Override
    //扫描目录对话框
	protected Dialog onCreateDialog(int id)
	{
		final SystemService systemService = new SystemService(this);
		//搜索音频数据库，将歌曲所在的目录全部返回
		Set<String> folderList = systemService.getFolderContainMedia();
        /**
         * folderList.toArray(T [])
         * 返回一个包含此 set 中所有元素的数组；返回数组的运行时类型是指定数组的类型。
         * 如果指定的数组能容纳该 set，则它将在其中返回。
         * 否则，将分配一个具有指定数组的运行时类型和此 set 大小的新数组。
         */
		choices = folderList.toArray(new String[folderList.size()]);
		// 选项数组
		// String[] choices = { "Facebook", "Twitter" };
		// Check判断数组，与选项对应
		// boolean[] chsBool = { true, false };

		AlertDialog dialog = CommonAlertDialogBuilder.getInstance(this)
				.setIcon(R.drawable.ic_menu_scan).setTitle("请选择扫描目录")
				.setMultiChoiceItems(choices, null,
						new OnMultiChoiceClickListener()
                        {
							@Override
							public void onClick(DialogInterface dialog,
									int which, boolean isChecked)
                            {
								if (isChecked)
								{
									checkedItem.add(which);
								}
								else
								{
									checkedItem.remove((Object) which);
								}
							}
						}).setPositiveButton("Yes", new DialogInterface.OnClickListener()
                        {
                            //如果点了Yes
							@Override
							public void onClick(DialogInterface dialog, int which)
                            {
								showProcessDialog();
								// System.out.println(checkedItem.toString());
								for (int i = 0; i < checkedItem.size(); i++)
								{
                                    /**systemService.getMediasByFolder():
                                     * 在系统音频数据库里按路径path查找，返回包含前缀path的所有歌曲
                                     * 的路径集合
                                     */
									//addMediaToPlaylist:把这些歌添加到数据库的歌曲表格
									addMediaToPlaylist(systemService.getMediasByFolder(
											choices[checkedItem.get(i)]));
								}

								//mRunnable.run();
								checkedItem.clear();
							}

						}).setNegativeButton("No",
						new DialogInterface.OnClickListener()
                        {
                            //如果点了No
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								checkedItem.clear();
							}
						}).create();
		return dialog;
	}

	//从路径中截取文件名
	private String getMediaName(String path)
	{
		String mediaName = path.substring(path.lastIndexOf("/") + 1);
		return mediaName;
	}


    /**
     * @param medias 歌曲的路径集合
     */
	//把这些歌添加到自定义数据库的歌曲表格
	private final void addMediaToPlaylist(Set<String> medias)
    {
        //pId是播放列表的ID，每首歌都属于一张播放列表
		//这个Intent的数据从"播放列表"界面传来
		String pId = this.getIntent().getExtras().get(
				"com.hrw.android.player.pid").toString();
		ContentValues values = new ContentValues();
		for (String path : medias)
		{
			//查询自定义数据库，当前播放列表是否有名为name的歌曲
			if (getCountPlaylistMediaByName(getMediaName(path)) == 0)
			{
				Audio audio = new Audio();
				audio.setPlaylistId(pId);//设置歌曲属于哪个播放列表
				audio.setName(getMediaName(path));
				audio.setPath(path);
				audio.setAddDate(new Date());
				audio.setUpdateDate(new Date());
				values = bulid(audio);//将JavaBean转换成ContentValues形式
				audioDao.addMediaToPlaylist(values);//把这首歌添加到数据库的歌曲表格
			}
		}
		initListAdapter();//更新列表数据
		Toast.makeText(this, "添加音乐成功", Toast.LENGTH_LONG).show();
	}

	//查询自定义数据库，当前播放列表是否有名为name的歌曲
	private final int getCountPlaylistMediaByName(String name)
    {
		ContentResolver resolver = getContentResolver();
		Uri uri = Uri
				.parse("content://" + UriConstant.AUTHORITY + "/audiolist");
		String[] proj = { "id" };
		// String
		// selection=MediaStore.Audio.Media.DATA+" like '/mnt/sdcard/Recording/%'";
		String selection = "playlist_id = ? AND audio_name = ?";
		String[] selectionArgs = {
				this.getIntent().getExtras().get("com.hrw.android.player.pid")
						.toString(), name };
		Cursor cursor = resolver.query(uri, proj, selection, selectionArgs,
				null);
		return cursor.getCount();
	}

	private void initButtons()
    {
		final TabActivity tabActivity = (TabActivity) getParent();
		LinearLayout addAudioBtn = (LinearLayout) findViewById(R.id.create_audio_list_header);
		addAudioBtn.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				showDialog(1);//弹出activity的对话框，选择扫描目录
			}
		});
		back_btn = (ImageButton) tabActivity.findViewById(R.id.list_back);
		back_btn.setOnTouchListener(new OnTouchListener()
		{
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN)
				{

				}
				else if (event.getAction() == MotionEvent.ACTION_UP)
				{
					HomeActivity.tabHost.setCurrentTabByTag(Constants.TAB_SPEC.SongBookTab.getId());
					Intent updateUiIntent = new Intent(
							Constants.UPDATE_UI_ACTION);
					sendBroadcast(updateUiIntent);
				}
				return false;
			}
		});
	}

	public void initListAdapter()
	{
		musicList = audioDao.getAudioListByPlaylistId(this.getIntent()
				.getExtras().get("com.hrw.android.player.pid").toString());

		adapter = new MusicListAdapter(this, musicList, this.getIntent()
				.getExtras().get("com.hrw.android.player.pid").toString());
		setListAdapter(adapter);
		TextView count_audio = (TextView) findViewById(R.id.count_audio);
		count_audio.setText("共" + String.valueOf(musicList.size()) + "首");
	}


	@Override
	protected void onCreate(Bundle savedInstanceState)
    {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.audio_list);
		initPopupMenu();
		initButtons();
	}

	@Override
	protected void onResume()
	{
		initListAdapter();
		super.onResume();
	}


	@Override
	protected Set<Integer> getPopUpMenu() {
		if (adapter.getCheckedBoxPositionIds().size() > 0) {
			popUpMenu.add(PopupMenu.ADD_TO.getMenu());
		} else {
			popUpMenu.remove(PopupMenu.ADD_TO.getMenu());
		}
		return popUpMenu;
	}

	private void initPopupMenu()
    {
		popUpMenu.add(PopupMenu.ADD_TO.getMenu());
		popUpMenu.add(PopupMenu.CREATE_LIST.getMenu());
		popUpMenu.add(PopupMenu.EXIT.getMenu());
		popUpMenu.add(PopupMenu.HELP.getMenu());
		popUpMenu.add(PopupMenu.SETTING.getMenu());
	}
}
