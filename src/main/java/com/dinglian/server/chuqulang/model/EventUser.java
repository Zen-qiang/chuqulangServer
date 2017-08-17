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
import javax.persistence.UniqueConstraint;

@Table(name = "event_user", uniqueConstraints = { 
		@UniqueConstraint(columnNames = { "fk_event_id", "fk_user_id" })
})
@Entity
public class EventUser implements Serializable{
	
	private int id;
	
	private Event event;
	
	private User user;
	
	private int orderNo;
	
	private Date creationDate;
	
	private String realName;
	
	private String phoneNo;
	
	private Integer gender;//1时是男性，值为2时是女性，值为0时是未知
	
	private User inviter;// 邀请者
	
	private boolean effective;

	@GeneratedValue
	@Id
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@JoinColumn(name = "fk_event_id")
	@ManyToOne(fetch = FetchType.LAZY)
	public Event getEvent() {
		return event;
	}
	
	public void setEvent(Event event) {
		this.event = event;
	}
	

	@JoinColumn(name = "fk_user_id")
	@ManyToOne(fetch = FetchType.LAZY)
	public User getUser() {
		return user;
	}


	public void setUser(User user) {
		this.user = user;
	}

	@Column(name = "order_no")
	public int getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(int orderNo) {
		this.orderNo = orderNo;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "creation_date")
	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	
	@Column(name = "real_name")
	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	@Column(name = "phone_no")
	public String getPhoneNo() {
		return phoneNo;
	}

	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}

	@Column(name = "gender", length = 1)
	public Integer getGender() {
		return gender;
	}

	public void setGender(Integer gender) {
		this.gender = gender;
	}
	
	@JoinColumn(name = "fk_inviter_id")
	@ManyToOne(fetch = FetchType.LAZY)
	public User getInviter() {
		return inviter;
	}

	public void setInviter(User inviter) {
		this.inviter = inviter;
	}
	
	public boolean isEffective() {
		return effective;
	}

	public void setEffective(boolean effective) {
		this.effective = effective;
	}

	public EventUser() {
		// TODO Auto-generated constructor stub
	}

	public EventUser(Event event, User user, int orderNo) {
		super();
		this.event = event;
		this.user = user;
		this.orderNo = orderNo;
		this.creationDate = new Date();
	}

}
