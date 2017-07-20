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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

@Table(name = "coterie")
@Entity
public class Coterie implements Serializable{
	
	public static final String TYPE_HOT = "hot";
	public static final String TYPE_NEW = "new";

    private int id;
    
    private Tag tag;

    private String name; //圈子名称

    private String description; // 圈子备注

    private User creator; //创建人

    private int orderNo; //序号

    private int hot; //热度

    private Date creationDate; //创建时间
    
    private CoteriePicture coteriePicture; // 圈子图片
    
    private Set<CoterieGuy> coterieGuys = new HashSet<CoterieGuy>(); //圈子成员
    
    private Set<Topic> topics = new HashSet<Topic>(); //圈子成员
    
    @GeneratedValue
    @Id
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @JoinColumn(name = "fk_user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    @Column(name = "order_no")
    public int getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(int orderNo) {
        this.orderNo = orderNo;
    }

    public int getHot() {
        return hot;
    }

    public void setHot(int hot) {
        this.hot = hot;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "creation_date")
    public Date getCreationDate() {
    	return creationDate;
    }
    
    public void setCreationDate(Date creationDate) {
    	this.creationDate = creationDate;
    }

    @JoinColumn(name = "fk_tag_id")
    @ManyToOne(fetch = FetchType.LAZY)
	public Tag getTag() {
		return tag;
	}

	public void setTag(Tag tag) {
		this.tag = tag;
	}

	@JoinColumn(name = "fk_coterie_picture_id")
    @ManyToOne(fetch = FetchType.LAZY)
	public CoteriePicture getCoteriePicture() {
		return coteriePicture;
	}

	public void setCoteriePicture(CoteriePicture coteriePicture) {
		this.coteriePicture = coteriePicture;
	}

	@OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.ALL }, mappedBy = "coterie")
	public Set<CoterieGuy> getCoterieGuys() {
		return coterieGuys;
	}

	public void setCoterieGuys(Set<CoterieGuy> coterieGuys) {
		this.coterieGuys = coterieGuys;
	}

	@OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.ALL }, mappedBy = "coterie")
	public Set<Topic> getTopics() {
		return topics;
	}

	public void setTopics(Set<Topic> topics) {
		this.topics = topics;
	}

	@Transient
	public int getCoterieNextOrderNo() {
		int next = 0;
		for (CoterieGuy coterieGuy : this.getCoterieGuys()) {
			if (next < coterieGuy.getOrderNo()) {
				next = coterieGuy.getOrderNo();
			}
		}
		return next + 1;
	}

	@Transient
	public boolean isJoined(int userId) {
		for (CoterieGuy coterieGuy : this.getCoterieGuys()) {
			User user = coterieGuy.getUser();
			if (user.getId() == userId) {
				return true;
			}
		}
		return false;
	}
	
}
