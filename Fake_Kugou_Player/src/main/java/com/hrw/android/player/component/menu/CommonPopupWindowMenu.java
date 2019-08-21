package com.hrw.android.player.component.menu;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.PopupWindow;

import com.hrw.android.player.R;

//就是那个弹出选项框
public class CommonPopupWindowMenu extends PopupWindow
{
	private Context context;
	private View parent;

	private View child; //存放菜单子项的GridView
	ImageButton tab_menu;//功能菜单键

	public CommonPopupWindowMenu(Context context, View parent, View child)
	{
		super(context);
		tab_menu = (ImageButton) parent.findViewById(R.id.tab_menu);
		this.parent = parent;
		this.context = context;
		this.child = child;
		this.child.setFocusableInTouchMode(true);
		this.child.setOnKeyListener(new OnKeyListener()
		{
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event)
			{
				//如果点了菜单键，就取消显示弹出窗口（如果正在显示的话）
				if ((keyCode == KeyEvent.KEYCODE_MENU)
						&& (event.getAction() == KeyEvent.ACTION_DOWN)
						&& (isShowing()))
				{
					dismiss();
					return true;
				}
				return false;
			}
		});
		setContentView(child);
		setWidth(LayoutParams.FILL_PARENT);
		setHeight(LayoutParams.WRAP_CONTENT);
		setBackgroundDrawable(new BitmapDrawable());
		setFocusable(true);//弹出框可获得焦点

		setAnimationStyle(R.style.PopupAnimation);//弹出窗口的动画有疑问
		update();
	}

	public void show()
	{
		if ((this != null) && (!this.isShowing()))
		{
			tab_menu.setBackgroundResource(R.drawable.tab_menu_open_up);
			this.showAtLocation(parent, Gravity.BOTTOM, 0, 55);//有疑问
		}
	}

	@Override
	public void dismiss()
	{
		tab_menu.setSelected(false);
		tab_menu.setBackgroundResource(R.drawable.tab_menu_default);
		super.dismiss();
	}
}
