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

@Table(name = "topic_picture")
@Entity
public class TopicPicture implements Serializable {

	private int id;

	private Topic topic; // 话题

	private User user; // 上传人

	private String url; // 图片地址

	private int orderNo;

	private Date creationDate;

	@GeneratedValue
	@Id
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@JoinColumn(name = "fk_topic_id")
	@ManyToOne(fetch = FetchType.LAZY)
	public Topic getTopic() {
		return topic;
	}

	public void setTopic(Topic topic) {
		this.topic = topic;
	}

	@JoinColumn(name = "fk_user_id")
	@ManyToOne(fetch = FetchType.LAZY)
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "creation_date")
	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Column(name = "order_no")
	public int getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(int orderNo) {
		this.orderNo = orderNo;
	}

	public TopicPicture() {
		// TODO Auto-generated constructor stub
	}

	public TopicPicture(Topic topic, User user, String url, int orderNo) {
		super();
		this.topic = topic;
		this.user = user;
		this.url = url;
		this.orderNo = orderNo;
		this.creationDate = new Date();
	}
	
	
}
