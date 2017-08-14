package com.dinglian.server.chuqulang.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

/**
 * 话题
 * @author Mr.xu
 *
 */
@Table(name = "topic")
@Entity
public class Topic implements Serializable {
	
	public static final int TYPE_IMAGE_TEXT = 1;
	public static final int TYPE_VIDEO = 2;
	public static final int TYPE_ATIVITY = 3;
	
	public static final String DATATYPE_HISTROY = "histroy";
	
	private int id;
	
	private Coterie coterie;
	
	private User creator; //话题发起者
	
	private Integer topicType;
	
	private String description;// 文字
	
	private Set<TopicPicture> pictures = new HashSet<TopicPicture>(); // 图片
	
	private Event event;

	private Set<TopicComment> comments = new HashSet<TopicComment>();
	
	private Set<TopicPraise> praises = new HashSet<TopicPraise>();
	
	private Date creationDate;

	@GeneratedValue
	@Id
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@JoinColumn(name = "fk_coterie_id")
	@ManyToOne(fetch = FetchType.LAZY)
	public Coterie getCoterie() {
		return coterie;
	}

	public void setCoterie(Coterie coterie) {
		this.coterie = coterie;
	}

	@JoinColumn(name = "fk_user_id")
	@ManyToOne(fetch = FetchType.LAZY)
	public User getCreator() {
		return creator;
	}

	public void setCreator(User creator) {
		this.creator = creator;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.ALL }, mappedBy = "topic")
	public Set<TopicPicture> getPictures() {
		return pictures;
	}

	public void setPictures(Set<TopicPicture> pictures) {
		this.pictures = pictures;
	}

	@OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.ALL }, mappedBy = "topic")
	public Set<TopicComment> getComments() {
		return comments;
	}

	public void setComments(Set<TopicComment> comments) {
		this.comments = comments;
	}

	@OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.ALL }, mappedBy = "topic")
	public Set<TopicPraise> getPraises() {
		return praises;
	}

	public void setPraises(Set<TopicPraise> praises) {
		this.praises = praises;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "creation_date")
	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	
	@JoinColumn(name = "fk_event_id")
	@OneToOne(fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
	public Event getEvent() {
		return event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}
	
	@Column(name = "topic_type")
	public Integer getTopicType() {
		return topicType;
	}

	public void setTopicType(Integer topicType) {
		this.topicType = topicType;
	}

	public Topic() {
		// TODO Auto-generated constructor stub
	}

	public Topic(Coterie coterie, User creator, String description) {
		super();
		this.coterie = coterie;
		this.creator = creator;
		this.description = description;
		this.creationDate = new Date();
	}

	public Topic(int id) {
		super();
		this.id = id;
	}

	@Transient
	public int getNextOrderNo() {
		int orderNo = 0;
		for (TopicPraise praise : this.getPraises()) {
			if (orderNo < praise.getOrderNo()) {
				orderNo = praise.getOrderNo();
			}
		}
		return orderNo + 1;
	}
	
}
