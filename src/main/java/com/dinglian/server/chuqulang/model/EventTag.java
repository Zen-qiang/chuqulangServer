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

@Table(name = "event_tag")
@Entity
public class EventTag implements Serializable {

	private static final long serialVersionUID = 1L;

	private int id;

	private Event event;

	private Tag tag;

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

	@JoinColumn(name = "fk_tag_id")
	@ManyToOne(fetch = FetchType.LAZY)
	public Tag getTag() {
		return tag;
	}

	public void setTag(Tag tag) {
		this.tag = tag;
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

	public EventTag() {
		// TODO Auto-generated constructor stub
	}

	public EventTag(Event event, Tag tag, int orderNo) {
		super();
		this.event = event;
		this.tag = tag;
		this.orderNo = orderNo;
		this.creationDate = new Date();
	}

}
