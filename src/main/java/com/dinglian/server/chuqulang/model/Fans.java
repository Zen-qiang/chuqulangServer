package com.dinglian.server.chuqulang.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Table(name = "fans")
@Entity
public class Fans implements Serializable{

    private int id;

    private int orderNo;

    private User follower; //关注的人

    private User followedUser; //被关注的人

    private Date creationDate; //关注时间

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

    @JoinColumn(name = "fk_follower_id")
    @ManyToOne(fetch = FetchType.LAZY)
    public User getFollower() {
        return follower;
    }

    public void setFollower(User follower) {
        this.follower = follower;
    }

    @JoinColumn(name = "fk_followed_user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    public User getFollowedUser() {
        return followedUser;
    }

    public void setFollowedUser(User followedUser) {
        this.followedUser = followedUser;
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
