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

//	private Integer no;
	
	private int chatRoomId;// 云信聊天室ID

	private String name;
	
	private String announcement;// 公告

	private User creator; // 创建者

	private Date creationDate;
	
	private boolean valid;

//	private boolean open;

//	private String password;

	private Event event; // 所属活动

//	private Coterie coterie; // 所属圈子
	
	private Set<ChatNote> chatNotes = new HashSet<ChatNote>();

	@GeneratedValue
	@Id
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Column(name = "chatroom_id", unique = true)
	public int getChatRoomId() {
		return chatRoomId;
	}
	
	public void setChatRoomId(int chatRoomId) {
		this.chatRoomId = chatRoomId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@JoinColumn(name = "fk_user_id")
	@ManyToOne(fetch = FetchType.LAZY)
	public User getCreator() {
		return creator;
	}
	
	public void setCreator(User creator) {
		this.creator = creator;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "creation_date")
	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
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

	public String getAnnouncement() {
		return announcement;
	}

	public void setAnnouncement(String announcement) {
		this.announcement = announcement;
	}

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}
	
}
