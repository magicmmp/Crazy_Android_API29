package com.hrw.android.player.activity;

import android.app.ListActivity;
import android.content.ContentValues;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.hrw.android.player.R;
import com.hrw.android.player.adapter.menu.PopupWindowMenuAdapter;
import com.hrw.android.player.builder.ContentValuesBuilder;
import com.hrw.android.player.component.dialog.CreatePlaylistDialog;
import com.hrw.android.player.component.menu.CommonPopupWindowMenu;
import com.hrw.android.player.domain.BaseDomain;
import com.hrw.android.player.utils.Constants.PopupMenu;
import com.hrw.android.player.utils.MenuUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public abstract class BaseListActivity extends ListActivity
{
	CommonPopupWindowMenu popWindow;
	LinearLayout pop_menu_layout;//功能菜单布局
	Menu buttomMenu;//菜单子项合集
	ImageButton tab_menu;//功能菜单按钮

	protected abstract Set<Integer> getPopUpMenu();

	//用来做功能菜单按钮监听器
	OnTouchListener bottomMenuOnTouchListener = new OnTouchListener()
	{

		@Override
		public boolean onTouch(View v, MotionEvent event)
		{
			boolean selected = v.isSelected();
			if (event.getAction() == MotionEvent.ACTION_DOWN)
			{
				v.setBackgroundResource(R.drawable.tab_menu_pressed);
			}
			if (event.getAction() == MotionEvent.ACTION_UP)
			{
				if (!selected)
				{
					openOptionsMenu();//View方法
					v.setSelected(true);
					v.setBackgroundResource(R.drawable.tab_menu_open_up);
				} else
				{
					closeOptionsMenu();//View方法
					v.setSelected(false);
					v.setBackgroundResource(R.drawable.tab_menu_default);
				}

			}
			return false;
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		buttomMenu = MenuUtil.inflate(this, R.menu.pop_menu);//定义了所有菜单子项
		pop_menu_layout = (LinearLayout) this.getLayoutInflater().inflate(
				R.layout.pop_menu_layout, null, false);//功能菜单布局
	}

	private void initButton()
	{
		//功能菜单按钮
		tab_menu = (ImageButton) this.getParent().findViewById(R.id.tab_menu);
		tab_menu.setOnTouchListener(bottomMenuOnTouchListener);
	}

	@Override
	//Activity准备和用户交互时被回调
	protected void onResume()
	{
		initButton();
		super.onResume();
	}

	@Override
	//初始化Activity的标准选项菜单的内容
	//在这里放置菜单子项
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add("Ingore");//???
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onMenuOpened(int featureId, Menu menu)
	{
		// Toast.makeText(getBaseContext(),
		// "CurrentContext:" + this.getClass().getName(),
		// Toast.LENGTH_SHORT).show();
		if (null == popWindow)
		{
			pop_menu_layout = (LinearLayout) this.getLayoutInflater().inflate(
					R.layout.pop_menu_layout, null, false);
		}
		initPopupWindow(buttomMenu);
		popWindow.show();
		return false;// show system should return true?
	}

	@Override
	public void onOptionsMenuClosed(Menu menu)
	{
		popWindow.dismiss();//关闭弹出菜单窗口,是销毁了这个view？
	}

	private void initPopupWindow(Menu menu)
	{
		GridView gv = (GridView) pop_menu_layout.findViewById(R.id.menu_grid);
		List<MenuItem> menuList = new ArrayList<MenuItem>();
		Set<Integer> popUpMenu = getPopUpMenu();//需要子类重写的抽象方法?
		//这段有疑问
		for (int i = 0; i < menu.size(); i++) {
			if (popUpMenu.contains(menu.getItem(i).getOrder())) {
				menuList.add(menu.getItem(i));
			}
		}
		gv.setAdapter(new PopupWindowMenuAdapter(this.getParent(), menuList));
		gv.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				int menu = Integer.valueOf(String.valueOf(id));
				switch (menu)
				{
					case PopupMenu.ADD_ALL_TO_INDEX:
					{
						break;
					}
					case PopupMenu.ADD_TO_INDEX:
					{
						break;
					}
					case PopupMenu.CREATE_LIST_INDEX:
					{
						CreatePlaylistDialog.getCreatePlaylistDialog(
								BaseListActivity.this).create().show();
						break;
					}
					case PopupMenu.DELETE_INDEX:
					{
						break;
					}
					case PopupMenu.EXIT_INDEX: {
						android.os.Process.killProcess(android.os.Process.myPid());
						break;
					}
					case PopupMenu.HELP_INDEX: {
						break;
					}
					case PopupMenu.SCAN_INDEX: {
						break;
					}
					case PopupMenu.SETTING_INDEX: {
						break;
					}
					default: {
						break;
					}
				}
				Toast.makeText(getBaseContext(),
						"在adapter里的位置:" + position + "    Row id:" + id,
						Toast.LENGTH_SHORT).show();
				popWindow.dismiss();
			}
		});
		popWindow = new CommonPopupWindowMenu(getBaseContext(), this
				.getParent().findViewById(R.id.main_activity), pop_menu_layout);
		// popWindow.setOutsideTouchable(true);
	}

	//这里有疑问？
	protected <T extends BaseDomain> ContentValues bulid(T domain)
	{
		ContentValues cv = null;
		try
		{
			cv = ContentValuesBuilder.getInstance().bulid(domain);
		}
		catch (IllegalArgumentException e)
		{
			Log.e("BaseListActivity", e.getMessage());
		}
		catch (IllegalAccessException e)
		{
			Log.e("BaseListActivity", e.getMessage());
		}
		return cv;
	}
}
