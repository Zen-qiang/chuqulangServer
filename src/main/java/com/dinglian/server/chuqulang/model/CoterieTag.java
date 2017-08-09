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

@Table(name = "coterie_tag")
@Entity
public class CoterieTag implements Serializable {

	private static final long serialVersionUID = 1L;

	private int id;

	private Coterie coterie;

	private Tag tag;

	private int orderNo;// 如果是主标签，orderNo应当为负值，排序在最前

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

	public CoterieTag() {
		// TODO Auto-generated constructor stub
	}

	public CoterieTag(Coterie coterie, Tag tag, int orderNo) {
		super();
		this.coterie = coterie;
		this.tag = tag;
		this.orderNo = orderNo;
		this.creationDate = new Date();
	}

}
