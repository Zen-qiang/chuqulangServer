package com.dinglian.server.chuqulang.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Table(name = "news_material")
@Entity
public class NewsMaterial implements Serializable {

	private static final long serialVersionUID = 1L;

	private int id;

	private String mediaId;

	private String title;

	private String digest;

	private String url;// 如果是主标签，orderNo应当为负值，排序在最前

	private String thumbUrl;

	public NewsMaterial() {
		// TODO Auto-generated constructor stub
	}
	
	public NewsMaterial(String mediaId, String title, String digest, String url, String thumbUrl) {
		super();
		this.mediaId = mediaId;
		this.title = title;
		this.digest = digest;
		this.url = url;
		this.thumbUrl = thumbUrl;
	}

	@GeneratedValue
	@Id
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDigest() {
		return digest;
	}

	public void setDigest(String digest) {
		this.digest = digest;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Column(name = "thumb_url")
	public String getThumbUrl() {
		return thumbUrl;
	}

	public void setThumbUrl(String thumbUrl) {
		this.thumbUrl = thumbUrl;
	}

	@Column(name = "media_id")
	public String getMediaId() {
		return mediaId;
	}

	public void setMediaId(String mediaId) {
		this.mediaId = mediaId;
	}
	
}
