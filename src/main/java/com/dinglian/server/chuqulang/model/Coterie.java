package com.dinglian.server.chuqulang.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

@Table(name = "coterie")
@Entity
public class Coterie implements Serializable {

	private static final long serialVersionUID = 1L;
	public static final String TYPE_HOT = "hot";
	public static final String TYPE_NEW = "new";

	// 我的圈子分类
	public static final String DATATYPE_ALL = "1";
	public static final String DATATYPE_CREATED = "2";
	public static final String DATATYPE_ATTENTION = "3";
	
	public static final int STATUS_NORMAL = 1;
	public static final int STATUS_DISMISSING = 2;
	public static final int STATUS_DISMISSED = 3;

	private int id;

	private List<CoterieTag> tags = new ArrayList<CoterieTag>(); // 圈子标签

	private String name; // 圈子名称

	private String description; // 圈子备注

	private User creator; // 创建人

	private int hot; // 活跃度/热度

	private Date creationDate; // 创建时间

	private CoteriePicture coteriePicture; // 圈子图片
	
	private Set<CoterieGuy> coterieGuys = new HashSet<CoterieGuy>(); // 圈子成员

	private Set<Topic> topics = new HashSet<Topic>(); // 圈子话题
	
	private List<Event> events = new ArrayList<Event>(); //圈子活动
	
	private int status;

	@GeneratedValue
	@Id
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(length = 1000)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@JoinColumn(name = "fk_user_id")
	@ManyToOne(fetch = FetchType.LAZY)
	public User getCreator() {
		return creator;
	}

	public void setCreator(User creator) {
		this.creator = creator;
	}

	public int getHot() {
		return hot;
	}

	public void setHot(int hot) {
		this.hot = hot;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "creation_date")
	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	@JoinColumn(name = "fk_coterie_picture_id")
	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
	public CoteriePicture getCoteriePicture() {
		return coteriePicture;
	}

	public void setCoteriePicture(CoteriePicture coteriePicture) {
		this.coteriePicture = coteriePicture;
	}

	@OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.ALL }, mappedBy = "coterie")
	public Set<CoterieGuy> getCoterieGuys() {
		return coterieGuys;
	}

	public void setCoterieGuys(Set<CoterieGuy> coterieGuys) {
		this.coterieGuys = coterieGuys;
	}

	@OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.ALL }, mappedBy = "coterie")
	public Set<Topic> getTopics() {
		return topics;
	}

	public void setTopics(Set<Topic> topics) {
		this.topics = topics;
	}

	@OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.ALL }, mappedBy = "coterie", orphanRemoval = true)
	public List<CoterieTag> getTags() {
		Collections.sort(tags, new Comparator<CoterieTag>() {
			@Override
			public int compare(CoterieTag o1, CoterieTag o2) {
				return o1.getOrderNo() - o2.getOrderNo();
			}
		});
		return tags;
	}

	public void setTags(List<CoterieTag> tags) {
		this.tags = tags;
	}
	
	@OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.ALL }, mappedBy = "coterie")
	public List<Event> getEvents() {
		return events;
	}

	public void setEvents(List<Event> events) {
		this.events = events;
	}
	
	@Column(length = 1, nullable = false)
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	@Transient
	public int getCoterieGuyNextOrderNo() {
		int next = 0;
		for (CoterieGuy coterieGuy : this.getCoterieGuys()) {
			if (next < coterieGuy.getOrderNo()) {
				next = coterieGuy.getOrderNo();
			}
		}
		return next + 1;
	}

	@Transient
	public boolean isJoined(int userId) {
		for (CoterieGuy coterieGuy : this.getCoterieGuys()) {
			User user = coterieGuy.getUser();
			if (user.getId() == userId) {
				return true;
			}
		}
		return false;
	}

	@Transient
	public boolean isCreator(int userId) {
		if (this.getCreator().getId() == userId) {
			return true;
		}
		return false;
	}

}
