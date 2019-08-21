package com.hrw.android.player.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.hrw.android.player.BelmotPlayer;
import com.hrw.android.player.R;
import com.hrw.android.player.activity.LocalMusicListActivity;
import com.hrw.android.player.activity.MusicListActivity;
import com.hrw.android.player.activity.SearchMusicActivity;
import com.hrw.android.player.component.dialog.CommonAlertDialogBuilder;
import com.hrw.android.player.component.dialog.PlaylistDialog;
import com.hrw.android.player.dao.AudioDao;
import com.hrw.android.player.dao.impl.AudioDaoImpl;
import com.hrw.android.player.domain.Audio;
import com.hrw.android.player.media.PlayerEngineImpl.PlaybackMode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * 这是歌曲列表的适配器
 * 被本地音乐和自定义播放列表  使用
 */
public class MusicListAdapter extends BaseAdapter
{
	private List<Boolean> checkBoxesStatus;//列表中的每首歌曲的选择情况
	private Context context;
	private Set<Integer> checkedBoxIds;//被选中的歌曲ID
	private BelmotPlayer belmotPlayer;
	private List<Audio> audioList;
	private List<String> checkedAudioPathList;
	private List<String> checkedAudioIdList;
	private CharSequence[] longClickDialogItems;
    /**
     * playlistDialog:
     * 这是一个对话框，当长按本地或自定义的歌曲列表的某首歌，在弹出框选择"添加到播放列表"时，
     * 会弹出将这首歌添加到哪个自定义列表的对话框。
     */
	private PlaylistDialog playlistDialog;
	private AudioDao audioDao;
	private String playlistId;//播放列表的编号

	public MusicListAdapter(Context context, List<Audio> audioList, String playlistId)
	{
		super();
		this.context = context;
		this.audioList = audioList;
		this.playlistId = playlistId;
		initCheckBoxsStaus();
		initApplication();
		initAudioDao();
	}

	private void initAudioDao()
	{
		audioDao = new AudioDaoImpl(this.context);
	}

	private void initPlaylistDialog(final int position)
	{
		playlistDialog = new PlaylistDialog(context, getCheckedAudioPathList()
				.size() > 0 ? checkedAudioPathList.toArray(new String[] {})
				: new String[] { ((Audio) getItem(position)).getPath() });
	}

	private void initApplication()
	{
		if (null == belmotPlayer)
		{
			belmotPlayer = BelmotPlayer.getInstance();
		}
	}

	private void initCheckBoxsStaus()
	{
		checkBoxesStatus = new ArrayList<Boolean>(audioList.size());
		for (int i = 0; i < audioList.size(); i++)
		{
			checkBoxesStatus.add(false);
		}
		checkedBoxIds = new HashSet<Integer>();
	}

    /**
     * @return 被勾选的歌曲的文件路径
     */
	public List<String> getCheckedAudioPathList()
    {
		checkedAudioPathList = new ArrayList<String>();
		Iterator<Integer> iterator = getCheckedBoxPositionIds().iterator();
		while (iterator.hasNext())
		{
			checkedAudioPathList.add(audioList.get(iterator.next()).getPath());
		}
		return checkedAudioPathList;
	}

    /**
     * @return 被勾选歌曲的数据库里的ID
     */
	public List<String> getCheckedAudioIdList()
	{
		checkedAudioIdList = new ArrayList<String>();
		Iterator<Integer> iterator = getCheckedBoxPositionIds().iterator();
		while (iterator.hasNext())
		{
			checkedAudioIdList.add(audioList.get(iterator.next()).getId()
					.toString());
		}
		return checkedAudioIdList;
	}

	@Override
	public int getCount()
    {
		return audioList.size();
	}

	@Override
	public Object getItem(int position)
	{
		return audioList.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		return audioList.get(position).getId();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent)
	{
		Boolean checkBoxStatus = checkBoxesStatus.get(position);
		final ViewHolder holder;
		if (convertView == null)
		{
			convertView = LayoutInflater.from(context).inflate(
					R.layout.audio_list_item, null);

			holder = new ViewHolder();
			holder.index_tv = (TextView) convertView
					.findViewById(R.id.position_icon);
			holder.name_tv = (TextView) convertView.findViewById(R.id.line1);
			holder.play_btn = (ImageButton) convertView
					.findViewById(R.id.play_btn);
			holder.check_box = (CheckBox) convertView
					.findViewById(R.id.checkBox);
			convertView.setTag(holder);
		}
		else
		{
			holder = (ViewHolder) convertView.getTag();
		}
		holder.check_box.setChecked(checkBoxStatus);
		//把歌曲的勾选情况同步到checkBoxesStatus数组
        holder.check_box.setTag(position);
		holder.check_box.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{
				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked)
				{
					checkBoxesStatus.set((Integer) buttonView.getTag(),
							isChecked);
					// notifyDataSetChanged();
				}
		});
		holder.index_tv.setText(String.valueOf(position + 1) + ".");
		holder.name_tv.setText(audioList.get(position).getName());
		//这这里注册监听器合适吗？
        //对每个歌曲子项注册监听器
		this.setOnClickListener(position, convertView);
		this.setOnLongClickListener(position, convertView);
		this.initPlayerEngineView(position, holder);
		return convertView;
	}

	static class ViewHolder
	{
		private TextView index_tv;
		private TextView name_tv;
		private ImageButton play_btn;
		private CheckBox check_box;
	}

	private void initPlayerEngineView(final int position, ViewHolder holder)
    {
		if (null != belmotPlayer.getPlayerEngine())
		{
			String path = audioList.get(position).getPath();
			if (belmotPlayer.getPlayerEngine().isPlaying()
					&& belmotPlayer.getPlayerEngine().getPlayingPath().equals(path))
			{
				holder.index_tv.setPadding(30, 0, 0, 0);
				holder.play_btn.setVisibility(ImageButton.VISIBLE);
				holder.play_btn
						.setImageResource(R.drawable.list_playing_indicator);
			}
			else if (belmotPlayer.getPlayerEngine().isPause()
					&& belmotPlayer.getPlayerEngine().getPlayingPath().equals(
							path))
			{

				holder.index_tv.setPadding(30, 0, 0, 0);
				holder.play_btn.setVisibility(ImageButton.VISIBLE);
				holder.play_btn
						.setImageResource(R.drawable.list_pause_indicator);

			}
			else
			{
				holder.index_tv.setPadding(10, 0, 0, 0);
				holder.play_btn.setVisibility(ImageButton.INVISIBLE);
			}
		}

	}

	private void setOnClickListener(final int position, View convertView)
	{
		convertView.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
            {
				String path = ((Audio) getItem(position)).getPath();
				play(path);
				notifyDataSetChanged();
			}

		});
	}

	private void setOnLongClickListener(final int position, View convertView)
    {
		convertView.setOnLongClickListener(new OnLongClickListener()
        {
			@Override
			public boolean onLongClick(View v)
            {
				showItemLongClickDialog(position);
				return false;
			}
		});
	}


    /**
     * @return 被勾选的歌曲的index
     */
	public Set<Integer> getCheckedBoxPositionIds()
    {
		for (int i = 0; i < checkBoxesStatus.size(); i++)
		{
			if (checkBoxesStatus.get(i))
			{
				checkedBoxIds.add(i);
			}
			else if (checkedBoxIds.contains(i))
			{
				checkedBoxIds.remove(i);
			}
		}
		return checkedBoxIds;
	}

	public void next()
	{
		belmotPlayer.getPlayerEngine().next();
		notifyDataSetChanged();
	}

	private void initPlayerEngine()
	{
		List<String> audioPathList = new ArrayList<String>();
		if (!audioList.isEmpty())
		{
			for (Audio audio : audioList)
			{
				audioPathList.add(audio.getPath());
			}
			belmotPlayer.getPlayerEngine().setMediaPathList(audioPathList);
		}
		belmotPlayer.getPlayerEngine().setOnCompletionListener(
				new OnCompletionListener() {
					@Override
					public void onCompletion(MediaPlayer mp) {
						next();
					}
				});
		belmotPlayer.getPlayerEngine().setPlaybackMode(PlaybackMode.NORMAL);

	}

	private void play(String path)
    {
		initPlayerEngine();
		if (belmotPlayer.getPlayerEngine().isPlaying()
				&& belmotPlayer.getPlayerEngine().getPlayingPath().equals(path))
		{
			belmotPlayer.getPlayerEngine().pause();
		}
		else if (belmotPlayer.getPlayerEngine().isPause()
				&& belmotPlayer.getPlayerEngine().getPlayingPath().equals(path))
		{
			belmotPlayer.getPlayerEngine().start();
		}
		else
		{
			if (belmotPlayer.getPlayerEngine().isPlaying()
					|| belmotPlayer.getPlayerEngine().isPause())
			{
				belmotPlayer.getPlayerEngine().reset();
			}
			belmotPlayer.getPlayerEngine().setPlayingPath(path);
			belmotPlayer.getPlayerEngine().play();
		}
	}

	private void showItemLongClickDialog(final int position)
    {
		AlertDialog.Builder builder = CommonAlertDialogBuilder
				.getInstance(context);
		final String path = ((Audio) getItem(position)).getPath();
		//根据不同的界面，显示对应弹出框内容
		if (context instanceof LocalMusicListActivity)
		{
			if (path == belmotPlayer.getPlayerEngine().getPlayingPath()
					&& belmotPlayer.getPlayerEngine().isPlaying())
			{
				longClickDialogItems = new String[] { "暂停", "添加到播放列表" };
			}
			else
			{
				longClickDialogItems = new String[] { "播放", "添加到播放列表" };
			}
		}
		else if (context instanceof MusicListActivity)
		{
			if (path == belmotPlayer.getPlayerEngine().getPlayingPath()
					&& belmotPlayer.getPlayerEngine().isPlaying()) {
				longClickDialogItems = new String[] { "暂停", "移除歌曲" };
			} else {
				longClickDialogItems = new String[] { "播放", "移除歌曲" };
			}
		}
		else if (context instanceof SearchMusicActivity)
		{
			if (path == belmotPlayer.getPlayerEngine().getPlayingPath()
					&& belmotPlayer.getPlayerEngine().isPlaying())
			{
				longClickDialogItems = new String[] { "暂停", "添加到播放列表" };
			}
			else
			{
				longClickDialogItems = new String[] { "播放", "添加到播放列表" };
			}
		}
		builder.setAdapter(
				new ArrayAdapter<Object>(context, R.layout.alert_dialog_item,
						longClickDialogItems),
				new DialogInterface.OnClickListener()
                {
					@Override
					public void onClick(DialogInterface dialog, int which)
                    {
						switch (which)
                        {
                            case 0:
                                play(path);
                                notifyDataSetChanged();
                                break;
                            case 1:
                                if (longClickDialogItems[which].equals("移除歌曲")
                                        && !playlistId.equals("")
                                        && null != playlistId)
                                {
                                    if (getCheckedBoxPositionIds().size() > 0)
                                    {
                                        String[] ids = getCheckedAudioIdList()
                                                .toArray(new String[] {});
                                        for (int i = 0; i < ids.length; i++)
                                        {
                                            audioDao.removeAudioFromPlaylist(ids[i], playlistId);
                                        }

                                    }
                                    else
                                    {

                                        audioDao.removeAudioFromPlaylist(
                                                ((Audio) getItem(position)).getId()
                                                        .toString(), playlistId);
                                    }

                                    MusicListActivity mla = (MusicListActivity) context;
                                    mla.initListAdapter();
                                }
                                else
                                {
                                    //不是前两项就是"添加到播放列表"
                                    initPlaylistDialog(position);
                                    playlistDialog.show();
                                }
                                break;
                            default:
                                break;
						}

					}
				}).setTitle(((Audio) getItem(position)).getName());
		AlertDialog alert = builder.create();
		alert.show();
	}
}
