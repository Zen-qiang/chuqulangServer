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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Table(name = "chat_room")
@Entity
public class ChatRoom implements Serializable{

	private int id;

	private Integer no;

	private String name;

	private User owner;

	private Date creationDate;

	private boolean open;

	private String password;

	private Event event; // 所属活动

	private Coterie coterie; // 所属圈子
	
	private Set<ChatNote> chatNotes = new HashSet<ChatNote>();

	@GeneratedValue
	@Id
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Integer getNo() {
		return no;
	}

	public void setNo(Integer no) {
		this.no = no;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@JoinColumn(name = "fk_user_id")
	@ManyToOne(fetch = FetchType.LAZY)
	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "creation_date")
	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	@Column(name = "is_open")
	public boolean isOpen() {
		return open;
	}
	
	public void setOpen(boolean open) {
		this.open = open;
	}
	
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.REMOVE }, mappedBy = "chatRoom")
	public Set<ChatNote> getChatNotes() {
		return chatNotes;
	}

	public void setChatNotes(Set<ChatNote> chatNotes) {
		this.chatNotes = chatNotes;
	}

//	@JoinColumn(name = "fk_event_id")
//	@ManyToOne(fetch = FetchType.LAZY)
	@OneToOne(mappedBy="chatRoom")
	public Event getEvent() {
		return event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}

	@JoinColumn(name = "fk_coterie_id")
	@ManyToOne(fetch = FetchType.LAZY)
	public Coterie getCoterie() {
		return coterie;
	}

	public void setCoterie(Coterie coterie) {
		this.coterie = coterie;
	}
}
