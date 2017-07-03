package com.dinglian.server.chuqulang.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Table(name = "user_collect", uniqueConstraints = { 
		@UniqueConstraint(columnNames = { "fk_coterie_id", "fk_user_id" }),
		@UniqueConstraint(columnNames = { "fk_event_id", "fk_user_id" }) 
})
@Entity
public class UserCollect implements Serializable {

	private int id;

	private Coterie coterie;

	private Event event;

	private int orderNo;

	private User user;

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

	@JoinColumn(name = "fk_event_id")
	@ManyToOne(fetch = FetchType.LAZY)
	public Event getEvent() {
		return event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}

	@Column(name = "order_no")
	public int getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(int orderNo) {
		this.orderNo = orderNo;
	}

	@JoinColumn(name = "fk_user_id")
	@ManyToOne(fetch = FetchType.LAZY)
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "creation_date")
	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public UserCollect() {
		// TODO Auto-generated constructor stub
	}

	public UserCollect(Event event, int orderNo, User user) {
		super();
		this.event = event;
		this.orderNo = orderNo;
		this.user = user;
		this.creationDate = new Date();
	}

}
