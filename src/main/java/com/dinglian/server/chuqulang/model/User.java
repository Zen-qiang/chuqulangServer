package com.dinglian.server.chuqulang.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

@Table(name = "user")
@Entity
public class User implements Serializable{

	public static final String CURRENT_USER = "currentUser";
	
	public static final String LOGIN_TYPE_USERNAME = "username";
	public static final String LOGIN_TYPE_CHECKCODE = "checkCode";
	
	public static final String REALM_USERNAME = "UserName";
	public static final String REALM_CHECKCODE = "CheckCode";

	private int id;

	private Integer regNo;

	private String regPhone;
	
//	private String userName;

	private String password;

	private String phoneNo;

	private String nickName;

	private String picture;

	private String signLog;

//	private String verifyNo;

	private String lastLoginIp;

	private String lastLoginCity;

	private Date lastLoginDate;

	private String lastLoginPhone;

//	private double balance;

	private TypeName typeName;

	private boolean active; //是否可用

	private String description; //状态描述

	private boolean vip; //会员
	
	private Set<Event> eventSet = new HashSet<Event>();
	
	private Set<UserCollect> userCollects = new HashSet<UserCollect>(); //用户收藏
	
	private Set<Contact> contacts = new HashSet<Contact>();

	private Set<UserInterest> interests = new HashSet<UserInterest>();
	
	private Set<UserAttention> attentions = new HashSet<UserAttention>();
	
	private Set<UserAttention> followers = new HashSet<UserAttention>();
	
	private String accid;
	
	private String token;
	
	// 拥有的聊天室
//	private Set<ChatRoom> ownedRooms = new HashSet<ChatRoom>();
	// 参与的聊天室
//	private Set<ChatGuy> joinedChatRooms = new HashSet<ChatGuy>();
	
	
	@GeneratedValue
	@Id
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Column(name = "reg_no")
	public Integer getRegNo() {
		return regNo;
	}

	public void setRegNo(Integer regNo) {
		this.regNo = regNo;
	}

	@Column(name = "reg_phone")
	public String getRegPhone() {
		return regPhone;
	}

	public void setRegPhone(String regPhone) {
		this.regPhone = regPhone;
	}

	/*@Column(name = "user_name",unique = true)
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}*/
	
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Column(name = "phone_no",unique = true)
	public String getPhoneNo() {
		return phoneNo;
	}

	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}

	@Column(name = "nick_name")
	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getPicture() {
		return picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}

	@Column(name = "sign_log")
	public String getSignLog() {
		return signLog;
	}

	public void setSignLog(String signLog) {
		this.signLog = signLog;
	}

	@Column(name = "last_login_ip")
	public String getLastLoginIp() {
		return lastLoginIp;
	}

	public void setLastLoginIp(String lastLoginIp) {
		this.lastLoginIp = lastLoginIp;
	}

	@Column(name = "last_login_city")
	public String getLastLoginCity() {
		return lastLoginCity;
	}

	public void setLastLoginCity(String lastLoginCity) {
		this.lastLoginCity = lastLoginCity;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "last_login_date")
	public Date getLastLoginDate() {
		return lastLoginDate;
	}

	public void setLastLoginDate(Date lastLoginDate) {
		this.lastLoginDate = lastLoginDate;
	}

	@Column(name = "last_login_phone")
	public String getLastLoginPhone() {
		return lastLoginPhone;
	}

	public void setLastLoginPhone(String lastLoginPhone) {
		this.lastLoginPhone = lastLoginPhone;
	}

	/*public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	@OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.REMOVE }, mappedBy = "user")
	public Set<UserInterest> getInterestSet() {
		return interestSet;
	}

	public void setInterestSet(Set<UserInterest> interestSet) {
		this.interestSet = interestSet;
	}*/

	@JoinColumn(name = "fk_type_name_id")
	@ManyToOne(fetch = FetchType.LAZY)
	public TypeName getTypeName() {
		return typeName;
	}

	public void setTypeName(TypeName typeName) {
		this.typeName = typeName;
	}

	@Column(name = "is_active")
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name = "is_vip")
	public boolean isVip() {
		return vip;
	}

	public void setVip(boolean vip) {
		this.vip = vip;
	}

	@OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.REMOVE }, mappedBy = "creator")
	public Set<Event> getEventSet() {
		return eventSet;
	}

	public void setEventSet(Set<Event> eventSet) {
		this.eventSet = eventSet;
	}
	
	@OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.REMOVE }, mappedBy = "user")
	public Set<UserCollect> getUserCollects() {
		return userCollects;
	}

	public void setUserCollects(Set<UserCollect> userCollects) {
		this.userCollects = userCollects;
	}
	
	@OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.ALL }, mappedBy = "user")
	public Set<UserInterest> getInterests() {
		return interests;
	}

	public void setInterests(Set<UserInterest> interests) {
		this.interests = interests;
	}
	
	@OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.ALL }, mappedBy = "user")
	public Set<Contact> getContacts() {
		return contacts;
	}

	public void setContacts(Set<Contact> contacts) {
		this.contacts = contacts;
	}
	
	@OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.ALL }, mappedBy = "user")
	public Set<UserAttention> getAttentions() {
		return attentions;
	}

	public void setAttentions(Set<UserAttention> attentions) {
		this.attentions = attentions;
	}

	@OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.ALL }, mappedBy = "attentionUser")
	public Set<UserAttention> getFollowers() {
		return followers;
	}

	public void setFollowers(Set<UserAttention> followers) {
		this.followers = followers;
	}
	
	public String getAccid() {
		return accid;
	}

	public void setAccid(String accid) {
		this.accid = accid;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public User() {
		// TODO Auto-generated constructor stub
	}

	public User(String phoneNo, String password) {
		super();
		this.password = password;
		this.phoneNo = phoneNo;
	}

	@Transient
	public int getUserCollectNextOrderNo() {
		int orderNo = 0;
		for (UserCollect collect : userCollects) {
			if (collect.getOrderNo() > orderNo) {
				orderNo = collect.getOrderNo();
			}
		}
		return orderNo + 1;
	}

	
}
