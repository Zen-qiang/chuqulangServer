package com.dinglian.server.chuqulang.model;

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

@Table(name = "user_attention_to", uniqueConstraints = { 
		@UniqueConstraint(columnNames = { "fk_user_id", "attention_user_id" })
})
@Entity
public class UserAttention {
	
	private int id;
	
	private User user;
	
	private User attentionUser;
	
	private Date creationDate;

	@GeneratedValue
	@Id
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@JoinColumn(name = "fk_user_id")
	@ManyToOne(fetch = FetchType.LAZY)
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@JoinColumn(name = "attention_user_id")
	@ManyToOne(fetch = FetchType.LAZY)
	public User getAttentionUser() {
		return attentionUser;
	}

	public void setAttentionUser(User attentionUser) {
		this.attentionUser = attentionUser;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "creation_date")
	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	
	

}
