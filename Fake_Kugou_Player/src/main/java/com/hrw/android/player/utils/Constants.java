package com.hrw.android.player.utils;

public class Constants
{
	public final static int MENU_TO_PLAYER_REQUEST_CODE = 0;
	public final static int MENU_TO_PLAYER_RESULT_CODE = 0;//这里好像写错了


	public final static String UPDATE_UI_ACTION = "com.hrw.android.updateui";

	//定义枚举类型
	//枚举其实是一个父类Enum的子类
	//定义所有的选项卡页面标号
	public static enum TAB_SPEC
	{
		SongBookTab("曲库"),
		LocalListTab("本地音乐"),
		MyListTab("播放列表"),
		SearchTab("搜索");

		//枚举类的构造函数
		private TAB_SPEC(String id)
		{
			this.id = id;
		}
		private String id;
		public String getId() {
			return this.id;
		}
	};

	//所有的菜单子项
	public enum PopupMenu
	{
		ADD_TO(1), ADD_ALL_TO(2), DELETE(3), CREATE_LIST(4), SCAN(5), SETTING(6), HELP(
				7), EXIT(8);
		private final int menu;

		private PopupMenu(int menu)
		{
			this.menu = menu;
		}

		public int getMenu()
		{
			return menu;
		}

		public final static int ADD_TO_INDEX = 1;
		public final static int ADD_ALL_TO_INDEX = 2;
		public final static int DELETE_INDEX = 3;
		public final static int CREATE_LIST_INDEX = 4;
		public final static int SCAN_INDEX = 5;
		public final static int SETTING_INDEX = 6;
		public final static int HELP_INDEX = 7;
		public final static int EXIT_INDEX = 8;

	}
}
