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

@Table(name = "contact", uniqueConstraints = { 
		@UniqueConstraint(columnNames = { "fk_user_id", "contact_user_id" })
})
@Entity
public class Contact implements Serializable {

	private int id;

	private int orderNo;

	private User user; // 所属用户

	private User contactUser; // 联系人

	private int degree; // 亲密度

	private String description; // 添加来源

	private Date creatonDate;

	@GeneratedValue
	@Id
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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

	@JoinColumn(name = "contact_user_id")
	@ManyToOne(fetch = FetchType.LAZY)
	public User getContactUser() {
		return contactUser;
	}

	public void setContactUser(User contactUser) {
		this.contactUser = contactUser;
	}

	public int getDegree() {
		return degree;
	}

	public void setDegree(int degree) {
		this.degree = degree;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "creation_date")
	public Date getCreatonDate() {
		return creatonDate;
	}

	public void setCreatonDate(Date creatonDate) {
		this.creatonDate = creatonDate;
	}

}
