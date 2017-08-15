package com.dinglian.server.chuqulang.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "tag")
@Entity
public class Tag implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public static final String TYPE_FIRST_LEVEL = "FIRST_LEVEL";
	public static final String TYPE_SECOND_LEVEL = "SECOND_LEVEL";
	
	public static final String TAG_STREET_DANCE = "街舞";
	public static final String TAG_BOARD_GAME = "桌游";
	public static final String TAG_OTHER = "其他";

	private int id;
	
	private String type; // 标签类型

	private String name;

	private int times; // 使用次数

	@GeneratedValue
	@Id
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getTimes() {
		return times;
	}

	public void setTimes(int times) {
		this.times = times;
	}

}
