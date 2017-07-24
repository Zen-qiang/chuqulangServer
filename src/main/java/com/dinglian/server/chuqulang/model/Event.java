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

	// 已关闭
	public static final String STATUS_OVER = "0";
	// 进行中
	public static final String STATUS_PROGRESS = "1";
	// 正在报名
	public static final String STATUS_SIGNUP = "2";
	// 好友参与
	public static final String STATUS_FRIENDS = "3";
	
	// 默认排序
	public static final String ORDERBY_DEFAULT = "0";
	// 距离最近
	public static final String ORDERBY_CLOSEST = "1";
	// 最新发布
	public static final String ORDERBY_LATEST = "2";
	// 价格由低到高
	public static final String ORDERBY_COST_ASC = "3";
	// 价格由高到低
	public static final String ORDERBY_COST_DESC = "4";
	
	// 全部活动
	public static final String DATATYPE_ALL = "0";
	// 我发起的
	public static final String DATATYPE_RELEASE = "1";
	// 我参与的
	public static final String DATATYPE_JOIN = "2";
	// 历史活动
	public static final String DATATYPE_EXPIRE = "3";
	
	private int id;

//	private String no; // 活动编号

	private String name; // 活动名称

	private String shortName; // 活动简称

	private String address; // 地址

	private String gps; // 活动经纬度

	private int userCount; // 活动人数

	private String description; // 活动介绍

	private TypeName typeName; // 活动类型

	private boolean open; // 是否公开

//	private boolean friendOnly; // 仅限好友

	private String password; // 活动密码

	private User creator; // 创建人

//	private Date rsTime; // 报名开始时间

//	private Date reTime; // 报名结束时间

	private Date startTime; // 活动开始时间

	private Date endTime; // 活动结束时间
	
	private Date creationDate; // 发布时间

	private String status; // 活动状态

	private String charge; // 费用类型
	
	private double cost; //活动费用

	private String limiter; // 限定条件

	private ChatRoom chatRoom; // 活动聊天室

	private Set<EventTag> tags = new HashSet<EventTag>(); // 活动标签

	private Set<EventUser> eventUsers = new HashSet<EventUser>();

	// private Set<EventVideo> eventVideos = new HashSet<EventVideo>();

	private Set<EventPicture> eventPictures = new HashSet<EventPicture>();// 活动图片（第一张封面）

	// private Set<EventLog> eventLogs = new HashSet<EventLog>();

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
	@Column(name = "creation_date")
	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	@Column(name = "status", length = 1)
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

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

	public String getCharge() {
		return charge;
	}

	public void setCharge(String charge) {
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
