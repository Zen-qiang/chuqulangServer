package com.dinglian.server.chuqulang.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Table(name = "message")
@Entity
public class Message implements Serializable{

    private int id;

    private int orderNo;

    private TypeName typeName;

    private boolean read; //已读

    private User user; //所属用户

    private User concatUser; //聊天对象

    private Date creationDate;

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

    @JoinColumn(name = "fk_typename_id")
    @ManyToOne(fetch = FetchType.LAZY)
    public TypeName getTypeName() {
        return typeName;
    }

    public void setTypeName(TypeName typeName) {
        this.typeName = typeName;
    }

    @Column(name = "is_read")
    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    @JoinColumn(name = "fk_user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @JoinColumn(name = "fk_concat_user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    public User getConcatUser() {
        return concatUser;
    }

    public void setConcatUser(User concatUser) {
        this.concatUser = concatUser;
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
