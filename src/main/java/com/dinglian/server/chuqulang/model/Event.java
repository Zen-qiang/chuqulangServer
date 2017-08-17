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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.dinglian.server.chuqulang.comparator.EventUserComparator;

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
	
	public static final String ORDER_BY_START_TIME = "startTime";
	
	private int id;
	
	private Coterie coterie; // 所属圈子

	private String name; // 活动名称

	private String address; // 地址

	private String gps; // 活动经纬度

	private int minCount; // 最小人数
	
	private int maxCount; // 最大人数

	private String description; // 活动介绍

	private boolean open; // 是否公开

	private String password; // 活动密码

	private User creator; // 创建人

	private Date startTime; // 活动开始时间

	private Date endTime; // 活动结束时间
	
	private Date creationDate; // 发布时间

	private String status; // 活动状态

	private String charge; // 费用类型
	
	private Double cost; //活动费用

	private String limiter; // 限定条件
	
	private boolean allowSignUp; // 允许报名
	
	private String phoneNo;

	private ChatRoom chatRoom; // 活动聊天室

	private List<EventTag> tags = new ArrayList<EventTag>(); // 活动标签

	private Set<EventUser> eventUsers = new HashSet<EventUser>();

	private Set<EventPicture> eventPictures = new HashSet<EventPicture>();// 活动图片（第一张封面）

	//微信端独有
	private Topic activityTopic;
	
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

	@Column(name = "min_count")
	public int getMinCount() {
		return minCount;
	}
	
	public void setMinCount(int minCount) {
		this.minCount = minCount;
	}
	
	@Column(name = "max_count")
	public int getMaxCount() {
		return maxCount;
	}
	
	public void setMaxCount(int maxCount) {
		this.maxCount = maxCount;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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

	@OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.ALL }, mappedBy = "event", orphanRemoval = true)
	public List<EventTag> getTags() {
		Collections.sort(tags, new Comparator<EventTag>() {
			@Override
			public int compare(EventTag o1, EventTag o2) {
				return o1.getOrderNo() - o2.getOrderNo();
			}
		});
		return tags;
	}

	public void setTags(List<EventTag> tags) {
		this.tags = tags;
	}

	@OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.ALL }, mappedBy = "event")
	public Set<EventUser> getEventUsers() {
		return eventUsers;
	}

	public void setEventUsers(Set<EventUser> eventUsers) {
		this.eventUsers = eventUsers;
	}

	@OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.ALL }, mappedBy = "event", orphanRemoval = true)
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

	public Double getCost() {
		return cost;
	}

	public void setCost(Double cost) {
		this.cost = cost;
	}

	public String getLimiter() {
		return limiter;
	}

	public void setLimiter(String limiter) {
		this.limiter = limiter;
	}
	
	@Column(name = "allow_sign_up")
	public boolean isAllowSignUp() {
		return allowSignUp;
	}
	
	public void setAllowSignUp(boolean allowSignUp) {
		this.allowSignUp = allowSignUp;
	}
	
	@Column(name = "phone_no")
	public String getPhoneNo() {
		return phoneNo;
	}

	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}

	@JoinColumn(name = "fk_chatroom_id", unique = true)
	@OneToOne(fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
	public ChatRoom getChatRoom() {
		return chatRoom;
	}

	public void setChatRoom(ChatRoom chatRoom) {
		this.chatRoom = chatRoom;
	}
	
	@JoinColumn(name = "fk_coterie_id")
	@ManyToOne(fetch = FetchType.LAZY)
	public Coterie getCoterie() {
		return coterie;
	}

	public void setCoterie(Coterie coterie) {
		this.coterie = coterie;
	}
	
	@OneToOne(mappedBy="event")
	public Topic getActivityTopic() {
		return activityTopic;
	}

	public void setActivityTopic(Topic activityTopic) {
		this.activityTopic = activityTopic;
	}
	
	@Transient
	public List<EventUser> getEffectiveMembers () {
		List<EventUser> data = new ArrayList<>();
		List<EventUser> resultList = new ArrayList<>(this.eventUsers);
		Collections.sort(resultList, new EventUserComparator());
		for (EventUser eventUser : resultList) {
			if (eventUser.isEffective()) {
				data.add(eventUser);
			}
		}
		return data;
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
			if(eventUser.getUser()==null){
				continue;
			}
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
	
	@Transient
	public boolean isCreator(int userId) {
		if (userId == this.getCreator().getId()) {
			return true;
		}
		return false;
	}

}
