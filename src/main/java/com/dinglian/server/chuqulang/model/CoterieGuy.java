package com.dinglian.server.chuqulang.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Table(name = "coterie_guy")
@Entity
public class CoterieGuy implements Serializable{

	private Integer id;

	private Coterie coterie; //所属圈子

	private int orderNo; //序号

	private User user; //成员

	private Date creationDate; //加入日期

	private boolean admin; //管理员

	private boolean allowed; //禁言

	@GeneratedValue
	@Id
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
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

	@Column(name = "is_admin")
	public boolean isAdmin() {
		return admin;
	}

	public void setAdmin(boolean admin) {
		this.admin = admin;
	}

	@Column(name = "is_allowed")
	public boolean isAllowed() {
		return allowed;
	}

	public void setAllowed(boolean allowed) {
		this.allowed = allowed;
	}
}
