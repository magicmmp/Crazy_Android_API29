package com.hrw.android.player.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.hrw.android.player.R;
import com.hrw.android.player.adapter.MenuListAdapter;
import com.hrw.android.player.utils.Constants;
import com.hrw.android.player.utils.Constants.PopupMenu;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
//这是"曲库"选项卡对应那个页面
//其实这个ListActivity只有2个子项：本地音乐和播放列表
public class MenuListActivity extends BaseListActivity {
	private List<String> menu_list;
	Set<Integer> popUpMenu = new HashSet<Integer>();
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		setContentView(R.layout.common_listview);
		initPopupMenu();
		initListAdapter();
		super.onCreate(savedInstanceState);
	}


	private void initListAdapter() {
		menu_list = new ArrayList<String>();
		menu_list.add("L");
		menu_list.add("P");
		setListAdapter(new MenuListAdapter(this, menu_list));
	}
/**
	private void toNextActivity(int paramInt, String tag, String label)
	{
		Intent nextActivity = this.arrayIntent[paramInt];
		TabActivity tabActivity = (TabActivity) getParent();
		TabHost.TabSpec tab_spec_music_list = tabActivity.getTabHost()
				.newTabSpec(tag).setIndicator(label);
		tab_spec_music_list.setContent(nextActivity);
		tabActivity.getTabHost().addTab(tab_spec_music_list);
		tabActivity.getTabHost().setCurrentTabByTag(tag);
	}
*/
	@Override
	protected void onListItemClick(ListView paramListView, View paramView,
			int paramInt, long paramLong)
	{
		super.onListItemClick(paramListView, paramView, paramInt, paramLong);
		switch (paramInt)
		{
			case 0:
			{
				HomeActivity.tabHost.setCurrentTabByTag(Constants.TAB_SPEC.LocalListTab.getId());
				break;
			}
			case 1:
			{
				HomeActivity.tabHost.setCurrentTabByTag(Constants.TAB_SPEC.MyListTab.getId());
				break;
			}
			default:
			{
				break;
			}
		}
	}

	@Override
	protected void onPause()
	{
		super.onPause();
	}

	@Override
	protected void onResume()
	{
		super.onResume();
	}

	@Override
	protected Set<Integer> getPopUpMenu()
	{
		return popUpMenu;
	}
	private void initPopupMenu()
	{
		popUpMenu.add(PopupMenu.CREATE_LIST.getMenu());
		popUpMenu.add(PopupMenu.EXIT.getMenu());
		popUpMenu.add(PopupMenu.HELP.getMenu());
		popUpMenu.add(PopupMenu.SETTING.getMenu());
	}

}
