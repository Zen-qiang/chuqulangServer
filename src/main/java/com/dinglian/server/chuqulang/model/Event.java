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

@Table(name = "event")
@Entity
public class Event implements Serializable {

	private int id;

	private String no; // 活动编号

	private String name; // 活动名称

	private String shortName; // 活动简称

	private String address; // 地址

	private String gps; // 活动经纬度

	private int userCount; // 活动人数

	private String description; // 活动介绍

	private TypeName typeName; // 活动类型

	private boolean open; // 是否公开

	private boolean friendOnly; // 仅限好友

	private String password; // 活动密密麻麻

	private User creator; // 创建人

	private Date startTime; // 报名开始时间

	private Date endTime; // 报名结束时间

	private Date rsTime; // 活动开始时间

	private Date reTime; // 活动结束时间

	private String nowStatus; // 活动状态

	private boolean charge; // 费用类型
	
	private double cost; //活动费用

	private String limiter; // 限定条件

	// private EventPicture coverPicture; // 活动图片（第一张封面）

	private ChatRoom chatRoom; // 活动聊天室

	private Set<EventTag> tags = new HashSet<EventTag>(); // 活动标签
	//
	private Set<EventUser> eventUsers = new HashSet<EventUser>();
	//
	// private Set<EventVideo> eventVideos = new HashSet<EventVideo>();
	//
	private Set<EventPicture> eventPictures = new HashSet<EventPicture>();// 活动图片（第一张封面）
	//
	// private Set<EventLog> eventLogs = new HashSet<EventLog>();

	@GeneratedValue
	@Id
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNo() {
		return no;
	}

	public void setNo(String no) {
		this.no = no;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "short_name")
	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getGps() {
		return gps;
	}

	public void setGps(String gps) {
		this.gps = gps;
	}

	@Column(name = "user_count")
	public int getUserCount() {
		return userCount;
	}

	public void setUserCount(int userCount) {
		this.userCount = userCount;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	/*
	 * @JoinColumn(name = "fk_tag_id")
	 * 
	 * @ManyToOne(fetch = FetchType.LAZY) public Tag getTag() { return tag; }
	 * 
	 * public void setTag(Tag tag) { this.tag = tag; }
	 */

	@JoinColumn(name = "fk_type_name_id")
	@ManyToOne(fetch = FetchType.LAZY)
	public TypeName getTypeName() {
		return typeName;
	}

	public void setTypeName(TypeName typeName) {
		this.typeName = typeName;
	}

	public boolean isOpen() {
		return open;
	}

	public void setOpen(boolean open) {
		this.open = open;
	}

	@Column(name = "is_friend_only")
	public boolean isFriendOnly() {
		return friendOnly;
	}

	public void setFriendOnly(boolean friendOnly) {
		this.friendOnly = friendOnly;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@JoinColumn(name = "fk_user_id")
	@ManyToOne(fetch = FetchType.LAZY)
	public User getCreator() {
		return creator;
	}

	public void setCreator(User creator) {
		this.creator = creator;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "start_time")
	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "end_time")
	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "rs_time")
	public Date getRsTime() {
		return rsTime;
	}

	public void setRsTime(Date rsTime) {
		this.rsTime = rsTime;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "re_time")
	public Date getReTime() {
		return reTime;
	}

	public void setReTime(Date reTime) {
		this.reTime = reTime;
	}

	@Column(name = "now_status")
	public String getNowStatus() {
		return nowStatus;
	}

	public void setNowStatus(String nowStatus) {
		this.nowStatus = nowStatus;
	}

	/*
	 * @JoinTable(name = "event_tag_map", joinColumns = {
	 * 
	 * @JoinColumn(name = "fk_event_id", referencedColumnName = "id") },
	 * inverseJoinColumns = {
	 * 
	 * @JoinColumn(name = "fk_tag_id", referencedColumnName = "id") })
	 * 
	 * @ManyToMany(fetch = FetchType.LAZY)
	 */

	@OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.ALL }, mappedBy = "event")
	public Set<EventTag> getTags() {
		return tags;
	}

	public void setTags(Set<EventTag> tags) {
		this.tags = tags;
	}

	@OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.ALL }, mappedBy = "event")
	public Set<EventUser> getEventUsers() {
		return eventUsers;
	}

	public void setEventUsers(Set<EventUser> eventUsers) {
		this.eventUsers = eventUsers;
	}

	@OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.ALL }, mappedBy = "event")
	public Set<EventPicture> getEventPictures() {
		return eventPictures;
	}

	public void setEventPictures(Set<EventPicture> eventPictures) {
		this.eventPictures = eventPictures;
	}

	/*
	 * @OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.REMOVE },
	 * mappedBy = "event") public Set<EventVideo> getEventVideos() { return
	 * eventVideos; }
	 * 
	 * public void setEventVideos(Set<EventVideo> eventVideos) {
	 * this.eventVideos = eventVideos; }
	 * 
	 * @OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.REMOVE },
	 * mappedBy = "event") public Set<EventLog> getEventLogs() { return
	 * eventLogs; }
	 * 
	 * public void setEventLogs(Set<EventLog> eventLogs) { this.eventLogs =
	 * eventLogs; }
	 */

	public boolean isCharge() {
		return charge;
	}
	
	public void setCharge(boolean charge) {
		this.charge = charge;
	}
	
	public double getCost() {
		return cost;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}

	public String getLimiter() {
		return limiter;
	}

	public void setLimiter(String limiter) {
		this.limiter = limiter;
	}

	@JoinColumn(name = "fk_chatroom_id", unique = true)
	@OneToOne(fetch = FetchType.LAZY)
	public ChatRoom getChatRoom() {
		return chatRoom;
	}

	public void setChatRoom(ChatRoom chatRoom) {
		this.chatRoom = chatRoom;
	}

	@Transient
	public EventPicture getCover() {
		for (EventPicture eventPicture : this.eventPictures) {
			if (eventPicture.getOrderNo() == 1) {
				return eventPicture;
			}
		}
		return null;
	}

	@Transient
	public boolean haveSignUp(int userId) {
		for (EventUser eventUser : this.eventUsers) {
			if (eventUser.getUser().getId() == userId) {
				return true;
			}
		}
		return false;
	}

	@Transient
	public int getEventUserMaxOrderNo() {
		int orderNo = 0;
		for (EventUser eventUser : this.eventUsers) {
			if (eventUser.getOrderNo() > orderNo) {
				orderNo = eventUser.getOrderNo();
			}
		}
		return orderNo;
	}

}
