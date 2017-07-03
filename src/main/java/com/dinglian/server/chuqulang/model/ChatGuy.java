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

@Table(name = "chat_guy")
@Entity
public class ChatGuy implements Serializable {

    private int id;

    private ChatRoom chatRoom;

    private User user;

    private int orderNo;

    private boolean allowed;

    @GeneratedValue
    @Id
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @JoinColumn(name = "fk_chatroom_id")
    @ManyToOne(fetch = FetchType.LAZY)
    public ChatRoom getChatRoom() {
        return chatRoom;
    }

    public void setChatRoom(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
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

    @Column(name = "is_allowed")
    public boolean isAllowed() {
        return allowed;
    }

    public void setAllowed(boolean allowed) {
        this.allowed = allowed;
    }
}
