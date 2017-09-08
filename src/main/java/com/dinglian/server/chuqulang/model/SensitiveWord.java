package com.dinglian.server.chuqulang.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "sensitive_word")
@Entity
public class SensitiveWord implements Serializable {

	public static final int FIRST_LEVEL = 1;
	public static final int SECOND_LEVEL = 2;
	public static final int THREE_LEVEL = 3;

	private int id;

	private String sensitiveWord;

	@GeneratedValue
	@Id
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Column(name = "sensitive_word")
	public String getSensitiveWord() {
		return sensitiveWord;
	}

	public void setSensitiveWord(String sensitiveWord) {
		this.sensitiveWord = sensitiveWord;
	}

}
