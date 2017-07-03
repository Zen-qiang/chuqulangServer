package com.dinglian.server.chuqulang.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Table(name = "type_name")
@Entity
public class TypeName implements Serializable{

    private int id;

    private String name;

    private String description;

    private int orderNo;

    private Date creationDate;

    @GeneratedValue
    @Id
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Column(name = "name", unique = true)
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
}
