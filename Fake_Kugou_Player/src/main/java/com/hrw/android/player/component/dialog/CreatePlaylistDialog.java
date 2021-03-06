package com.hrw.android.player.component.dialog;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

import com.hrw.android.player.R;
import com.hrw.android.player.dao.PlaylistDao;
import com.hrw.android.player.dao.impl.PlaylistDaoImpl;
import com.hrw.android.player.utils.ImageScale;

/**
 * 产生一个AlertDialog.Builder。
 * 用于新建一个播放列表时，弹出的对话框，可以编辑列表名。
 */
public class CreatePlaylistDialog extends CommonAlertDialogBuilder
{

	private static PlaylistDao playlistDao;

	public static Builder getCreatePlaylistDialog(final Context context)
	{
		playlistDao = new PlaylistDaoImpl(context);
		AlertDialog.Builder builder = getInstance(context);
		final EditText et = new EditText(context);
		et.setText(context
				.getString(R.string.create_playlist_create_text_prompt));
		et.setSelectAllOnFocus(true);
		builder.setView(et);
		builder.setPositiveButton(context.getString(R.string.save), new OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.cancel();
				createPlaylist(et.getText().toString());
				Toast.makeText(context, "添加播放列表成功", Toast.LENGTH_LONG)
						.show();
			}
		});
		builder.setNeutralButton(context.getString(R.string.dialog_cancel),
				null);
		//ImageScale.getImage将图标缩放
		builder.setIcon(ImageScale.getImage(context));
		builder.setTitle(R.string.create_playlist_create_text_prompt);

		return builder;
	}

	private final static void createPlaylist(String name) {
		playlistDao.createPlaylist(name);
	}

}
