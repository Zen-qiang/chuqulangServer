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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

@Table(name = "user")
@Entity
public class User implements Serializable {

	public static final String CURRENT_USER = "currentUser";

	public static final String LOGIN_TYPE_USERNAME = "username";
	public static final String LOGIN_TYPE_CHECKCODE = "checkCode";

	public static final String REALM_USERNAME = "UserName";
	public static final String REALM_CHECKCODE = "CheckCode";

	private int id;

	private String password;

	private String phoneNo;

	private String nickName;

	private String picture;

	private String signLog;

	private String lastLoginIp;

	private String lastLoginCity;

	private Date lastLoginDate;

	private String lastLoginPhone;
	// 是否可用
	private boolean active; 
	// 用户收藏
	private Set<UserCollect> userCollects = new HashSet<UserCollect>(); 

	private Set<Contact> contacts = new HashSet<Contact>();

	private Set<UserInterest> interests = new HashSet<UserInterest>();

	private Set<UserAttention> attentions = new HashSet<UserAttention>();

	private Set<UserAttention> followers = new HashSet<UserAttention>();

	// 网易ACCID
	private String accid;
	// 网易TOKEN
	private String token;
	// 微信OPENID
	private String openId;
	// 性别：1时是男性，值为2时是女性，值为0时是未知
	private Integer gender;
	// 生日
	private Date birthday;

	@GeneratedValue
	@Id
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Column(name = "phone_no", unique = true)
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

	@Column(name = "is_active")
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
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

	@Column(name = "accid", unique = true)
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

	@Column(name = "open_id", unique = true)
	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	@Column(name = "gender", length = 1)
	public Integer getGender() {
		return gender;
	}

	public void setGender(Integer gender) {
		this.gender = gender;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "birthday")
	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
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

	@Transient
	public int getContactNextOrder() {
		int orderNo = 0;
		for (Contact contact : contacts) {
			if (contact.getOrderNo() > orderNo) {
				orderNo = contact.getOrderNo();
			}
		}
		return orderNo + 1;
	}

}
