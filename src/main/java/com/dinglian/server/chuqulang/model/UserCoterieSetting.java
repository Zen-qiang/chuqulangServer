package com.dinglian.server.chuqulang.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Table(name = "user_coterie_setting", uniqueConstraints = { 
		@UniqueConstraint(columnNames = { "fk_user_id", "fk_coterie_id" })
})
@Entity
public class UserCoterieSetting implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private int id;
	
	private Coterie coterie;
	
	private User user;
	
	private boolean allowPush;

	@GeneratedValue
	@Id
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@JoinColumn(name = "fk_coterie_id", nullable = false)
	@ManyToOne(fetch = FetchType.LAZY)
	public Coterie getCoterie() {
		return coterie;
	}

	public void setCoterie(Coterie coterie) {
		this.coterie = coterie;
	}

	@JoinColumn(name = "fk_user_id", nullable = false)
	@ManyToOne(fetch = FetchType.LAZY)
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Column(name = "allow_push", nullable = false)
	public boolean isAllowPush() {
		return allowPush;
	}

	public void setAllowPush(boolean allowPush) {
		this.allowPush = allowPush;
	}
	
	

}
