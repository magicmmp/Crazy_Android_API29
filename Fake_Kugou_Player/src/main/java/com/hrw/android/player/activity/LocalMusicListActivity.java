package com.hrw.android.player.activity;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.hrw.android.player.R;
import com.hrw.android.player.adapter.MusicListAdapter;
import com.hrw.android.player.dao.AudioDao;
import com.hrw.android.player.dao.impl.AudioDaoImpl;
import com.hrw.android.player.domain.Audio;
import com.hrw.android.player.utils.Constants;
import com.hrw.android.player.utils.Constants.PopupMenu;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 此页面是选项卡页面，显示详细的本地音乐列表
 */
public class LocalMusicListActivity extends BaseListActivity
{
	//AudioDaoImpl是访问音频数据库的实现类
	private AudioDao audioDao = new AudioDaoImpl(this);
	private ImageButton back_btn;
	private List<Audio> musicList;
	private MusicListAdapter adapter;
	Set<Integer> popUpMenu = new HashSet<Integer>();

	// @Override
	// protected void onPrepareDialog(int id, Dialog dialog) {
	// AlertDialog alertDialog = (AlertDialog) dialog;
	// ListView lv = alertDialog.getListView();
	// for (int i = 0; i < choices.length; i++) {
	// lv.setItemChecked(i, false);
	// }
	// super.onPrepareDialog(id, dialog);
	// }

	//添加弹出菜单的子项
	private void initPopupMenu()
	{
		popUpMenu.add(PopupMenu.ADD_ALL_TO.getMenu());
		popUpMenu.add(PopupMenu.CREATE_LIST.getMenu());
		popUpMenu.add(PopupMenu.EXIT.getMenu());
		popUpMenu.add(PopupMenu.HELP.getMenu());
		popUpMenu.add(PopupMenu.SETTING.getMenu());
	}

	private void initButtons()
	{
		final TabActivity tabActivity = (TabActivity) getParent();
		final Intent toMenuListActivity = new Intent(this,
				MenuListActivity.class);

		//顶上的工具栏返回键
		back_btn = (ImageButton) tabActivity.findViewById(R.id.list_back);
		back_btn.setOnTouchListener(new OnTouchListener()
		{
			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				if (event.getAction() == MotionEvent.ACTION_DOWN)
				{

				}
				else if (event.getAction() == MotionEvent.ACTION_UP)
				{
					HomeActivity.tabHost.setCurrentTabByTag(Constants.TAB_SPEC.SongBookTab.getId());
					Intent updateUiIntent = new Intent(Constants.UPDATE_UI_ACTION);
					sendBroadcast(updateUiIntent);
				}
				return false;
			}
		});
	}

	private void initListAdapter()
	{
		//获取本地音乐列表
		musicList = audioDao.getLocalAudioList();
		adapter = new MusicListAdapter(this, musicList, null);
		setListAdapter(adapter);
		// this.getListView().setOnItemLongClickListener(
		// new OnItemLongClickListener() {
		// @Override
		// public boolean onItemLongClick(AdapterView<?> parent,
		// View view, int position, long id) {
		// showItemLongClickDialog(id);
		// return false;
		// }
		// });
	}

	// private void showItemLongClickDialog(final long id) {
	// AlertDialog.Builder builder = CommonAlertDialogBuilder
	// .getInstance(this);
	// final CharSequence[] items = { "重命名", "删除" };
	// // TODO setMessage is something different with kugou's
	// builder.setItems(items, new DialogInterface.OnClickListener() {
	// @Override
	// public void onClick(DialogInterface dialog, int which) {
	// // TODO Auto-generated method stub
	// switch (which) {
	// // TODO 0,1 to constant
	// case 0:
	// break;
	// case 1:
	// // playlistDao.removePlaylist(String.valueOf(id));
	// initListAdapter();
	// break;
	// default:
	// break;
	// }
	//
	// }
	// }).setTitle("id:" + id);
	// AlertDialog alert = builder.create();
	// alert.show();
	// }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.local_audio_list);
		initPopupMenu();
		initButtons();
		initListAdapter();
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		back_btn.setVisibility(ImageButton.VISIBLE);
		TextView top_title_tv = (TextView) this.getParent().findViewById(
				R.id.top_title);
		top_title_tv.setText(R.string.menu_local_music);
		adapter.notifyDataSetChanged();
	}

	@Override
	protected Set<Integer> getPopUpMenu()
	{
		//如果勾选了本地音乐列表的歌曲，
		//就增加一个"添加到..."的菜单子项
		if (adapter.getCheckedBoxPositionIds().size() > 0)
		{
			popUpMenu.add(PopupMenu.ADD_TO.getMenu());
		}
		else
		{
			popUpMenu.remove(PopupMenu.ADD_TO.getMenu());
		}
		return popUpMenu;
	}
}
