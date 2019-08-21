package com.hrw.android.player.domain;

import com.hrw.android.player.db.DataBaseHelper;
import com.hrw.android.player.orm.annotation.Column;
import com.hrw.android.player.orm.annotation.Table;

import java.util.Date;



@Table(name = DataBaseHelper.PLAYLIST_TABLE_NAME)
public class Playlist extends BaseDomain {

	private Long id;
	@Column(name = "name")
	private String name;
	@Column(name = "add_date")//数据库使用的列名
	private Date addDate;
	@Column(name = "modified_date")
	private Date updateDate;

	private Integer countAudio;//该播放列表有几首歌

	public Long getId() {
		return id;
	}

	public Integer getCountAudio() {
		return countAudio;
	}

	public void setCountAudio(Integer countAudio)
	{
		this.countAudio = countAudio;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getAddDate() {
		return addDate;
	}

	public void setAddDate(Date addDate) {
		this.addDate = addDate;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

}
