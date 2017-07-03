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

@Table(name = "event_user")
@Entity
public class EventUser implements Serializable{
	
	private int id;
	
	private Event event;
	
	private User user;
	
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
