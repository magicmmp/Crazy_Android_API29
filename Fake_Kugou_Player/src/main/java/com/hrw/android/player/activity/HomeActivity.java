package com.hrw.android.player.activity;

import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.TabHost;

import com.hrw.android.player.BelmotPlayer;
import com.hrw.android.player.R;
import com.hrw.android.player.broadcastreceiver.UpdateUiBroadcastReceiver;
import com.hrw.android.player.component.dialog.ExitDialog;
import com.hrw.android.player.dao.AudioDao;
import com.hrw.android.player.dao.impl.AudioDaoImpl;
import com.hrw.android.player.utils.Constants;

import java.util.List;

/**
 * 这是一个选项卡界面，顶上一个TopBar工具条，底下一排图片按钮，
 * 中间是一个典型的选项卡界面TabHost。
 *
 */

public class HomeActivity extends TabActivity
{
	private static final String TAG = "HomeActivity";
	public static TabHost tabHost;
	private BelmotPlayer belmotPlayer;
	ImageButton tab_main;
	ImageButton tab_random;
	ImageButton tab_search;
	ImageButton tab_menu;
	View tabButtonSelectd;
	private AudioDao audioDao;

	/**
	 * Launch Home activity helper
	 * 
	 * @param c
	 *            context where launch home from (used by SplashscreenActivity)
	 */
	/**
	public static void launch(Context c) {
		Intent intent = new Intent(c, HomeActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		c.startActivity(intent);
	}
*/


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.main_activity);
		super.onCreate(savedInstanceState);
		initTopButton();
		initAllTabHost();
		initBottomMenu();
		registerReceiver();
		if (null == belmotPlayer) {
			belmotPlayer = BelmotPlayer.getInstance();
		}
		if (null == audioDao) {
			audioDao = new AudioDaoImpl(this);
		}
	}

	private void registerReceiver()
	{
		IntentFilter localIntentFilter = new IntentFilter();
		localIntentFilter.addAction(Constants.UPDATE_UI_ACTION);
		BroadcastReceiver updateUiBroadcastReceiver = new UpdateUiBroadcastReceiver(
				this);
		registerReceiver(updateUiBroadcastReceiver, localIntentFilter);
	}

	private void initAllTabHost()
	{
		tabHost = getTabHost();
		Intent MenuListIntent = new Intent(this, MenuListActivity.class);
		TabHost.TabSpec MenuListSpec = tabHost.newTabSpec(
				Constants.TAB_SPEC.SongBookTab.getId()).setIndicator(
				Constants.TAB_SPEC.SongBookTab.getId());
		MenuListSpec.setContent(MenuListIntent);
		tabHost.addTab(MenuListSpec);

		Intent SearchIntent = new Intent(
				this, SearchMusicActivity.class);
		TabHost.TabSpec SearchSpec = tabHost.newTabSpec(
				Constants.TAB_SPEC.SearchTab.getId()).setIndicator(
				Constants.TAB_SPEC.SearchTab.getId());
		SearchSpec.setContent(SearchIntent);
		tabHost.addTab(SearchSpec);

		Intent LocalMusicIntent = new Intent(this, LocalMusicListActivity.class);
		TabHost.TabSpec LocalMusicSpec = tabHost.newTabSpec(
				Constants.TAB_SPEC.LocalListTab.getId()).setIndicator(
				Constants.TAB_SPEC.LocalListTab.getId());
		LocalMusicSpec.setContent(LocalMusicIntent);
		tabHost.addTab(LocalMusicSpec);

		Intent PlaylistIntent = new Intent(this, PlaylistActivity.class);
		TabHost.TabSpec PlaylistSpec = tabHost.newTabSpec(
				Constants.TAB_SPEC.MyListTab.getId()).setIndicator(
				Constants.TAB_SPEC.MyListTab.getId());
		PlaylistSpec.setContent(PlaylistIntent);
		tabHost.addTab(PlaylistSpec);

		tabHost.setCurrentTabByTag(Constants.TAB_SPEC.SongBookTab.getId());
	}


	private void initTopButton()
	{
		//此intent将跳到播放界面
		final Intent toPlayerActivity = new Intent(this, PlayerActivity.class);
		ImageButton player_button = (ImageButton) findViewById(R.id.player);
		player_button.setOnTouchListener(new OnTouchListener()
		{
			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				if (event.getAction() == MotionEvent.ACTION_UP)
				{
					startActivityForResult(toPlayerActivity,
							Constants.MENU_TO_PLAYER_REQUEST_CODE);
				}
				return false;
			}

		});
	}
/**
	private void switchActivity(Intent intent, String tag, String label)
	{
		TabHost.TabSpec tab_spec_music_list = this.getTabHost().newTabSpec(tag)
				.setIndicator(label);
		tab_spec_music_list.setContent(intent);
		this.getTabHost().addTab(tab_spec_music_list);
		this.getTabHost().setCurrentTabByTag(tag);
	}
*/
	OnClickListener bottomMenuOnClickListener = new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			boolean selected = v.isSelected();
			if (!selected)
			{
				tabButtonSelectd.setSelected(false);
				v.setSelected(true);
				tabButtonSelectd = v;
				switch (v.getId())
				{
					case R.id.tab_main:
						{
							tabHost.setCurrentTabByTag(Constants.TAB_SPEC.SongBookTab.getId());
							break;
						}
					case R.id.tab_random:
						{
							if (belmotPlayer.getPlayerEngine().isPlaying())
							{
								belmotPlayer.getPlayerEngine().stop();
							}
							List<String> musicList = audioDao.getLocalAudioPathList();
							belmotPlayer.getPlayerEngine().setMediaPathList(musicList);
							//这里有bug
							//假如手机里一首歌也没有，会出现下标越界问题
							belmotPlayer.getPlayerEngine().setPlayingPath(
									musicList.get(0));
							belmotPlayer.getPlayerEngine().play();
							break;
						}
					case R.id.tab_search:
						{
							tabHost.setCurrentTabByTag(Constants.TAB_SPEC.SearchTab.getId());
							break;
						}
				}
			}

			// else {
			// TODO: do something...
			// v.setSelected(true);
			// }

			// v.setSelected(true);
			// v.setBackgroundResource(R.drawable.tab_net_media_pressed);
		}
	};

	private void initBottomMenu()
	{
		// TODO It is the first button on the bottom.
		tab_main = (ImageButton) findViewById(R.id.tab_main);
		tab_random = (ImageButton) findViewById(R.id.tab_random);
		tab_search = (ImageButton) findViewById(R.id.tab_search);
		tab_menu = (ImageButton) findViewById(R.id.tab_menu);

		tab_main.setSelected(true);
		tabButtonSelectd = tab_main;
		// tab_random.setPressed(true);
		tab_main.setOnClickListener(bottomMenuOnClickListener);
		tab_random.setOnClickListener(bottomMenuOnClickListener);
		tab_search.setOnClickListener(bottomMenuOnClickListener);
	}

	@Override
	protected void onStart()
	{
		super.onStart();
		// localTabHost1.setCurrentTab(0);
	}

	@Override
	protected void onRestoreInstanceState(Bundle state) {
		// TODO Auto-generated method stub
		super.onRestoreInstanceState(state);
	}

	@Override
	//处理按键事件
	public boolean dispatchKeyEvent(KeyEvent event)
	{
		//如果按下返回键
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK)
		{
			if (event.getAction() == KeyEvent.ACTION_DOWN
					&& event.getRepeatCount() == 0) {
				ExitDialog.getExitDialog(this).create().show();
				return true;
			}
		}

		// else if (event.getKeyCode() == KeyEvent.KEYCODE_MENU) {
		// if (!popWindow.isShowing()) {
		// popWindow.showAtLocation(findViewById(R.id.bottom_bar),
		// Gravity.BOTTOM, 0, 55);
		// } else {
		// popWindow.dismiss();
		// }
		// return true;
		// }

		return super.dispatchKeyEvent(event);
	}

	@Override
	//从上一个活动返回时，回调这个方法
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		//如果是从播放界面返回这里
		if (requestCode == Constants.MENU_TO_PLAYER_REQUEST_CODE)
		{
			// Bundle bundle = data.getExtras();
			switch (resultCode) {
			case Constants.MENU_TO_PLAYER_RESULT_CODE:
				// File file = (File) bundle.get("filePath");
				// if (null != file) {
				// /music_list.add(file.getName());
				// }
				break;
			}
		}
	}
}
