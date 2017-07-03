package com.dinglian.server.chuqulang.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Table(name = "event_tag")
@Entity
public class EventTag implements Serializable{

    private int id;

    private Event event;
    
    private Tag tag;

    private int treeNo; //层级序号

    private int orderNo;

//    private String name;//标签名称

    private Date creationDate;

    @GeneratedValue
    @Id
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @JoinColumn(name = "fk_event_id")
    @ManyToOne(fetch = FetchType.LAZY)
    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }
    
    @JoinColumn(name = "fk_tag_id")
    @ManyToOne(fetch = FetchType.LAZY)
    public Tag getTag() {
		return tag;
	}

	public void setTag(Tag tag) {
		this.tag = tag;
	}

	@Column(name = "tree_no")
    public int getTreeNo() {
        return treeNo;
    }

    public void setTreeNo(int treeNo) {
        this.treeNo = treeNo;
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
    
    public EventTag() {
		// TODO Auto-generated constructor stub
	}

	public EventTag(Event event, Tag tag, int treeNo, int orderNo) {
		super();
		this.event = event;
		this.tag = tag;
		this.treeNo = treeNo;
		this.orderNo = orderNo;
		this.creationDate = new Date();
	}
    
}
