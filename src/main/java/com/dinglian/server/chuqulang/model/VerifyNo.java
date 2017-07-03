package com.dinglian.server.chuqulang.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Table(name = "verify_no")
@Entity
public class VerifyNo implements Serializable{
	
	public static final String VERIFY_NO_TYPE_REGISTER = "register";
	public static final String VERIFY_NO_TYPE_LOGIN = "login";
	public static final String VERIFY_NO_TYPE_FORGOT = "forgot";

	private int id;

	private String phoneNo;

	private String verifyNo;

	private String dataType;// register or login

	private Date creationTime;
	
	private boolean valid;

	@GeneratedValue
	@Id
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Column(name = "phone_no")
	public String getPhoneNo() {
		return phoneNo;
	}

	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}

	@Column(name = "verify_no")
	public String getVerifyNo() {
		return verifyNo;
	}

	public void setVerifyNo(String verifyNo) {
		this.verifyNo = verifyNo;
	}

	@Column(name = "data_type")
	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "creation_time")
	public Date getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(Date creationTime) {
		this.creationTime = creationTime;
	}
	
	@Column(name = "is_valid")
	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

	public VerifyNo() {
		// TODO Auto-generated constructor stub
	}

	public VerifyNo(String phoneNo, String verifyNo, String dataType, Date creationTime) {
		super();
		this.phoneNo = phoneNo;
		this.verifyNo = verifyNo;
		this.dataType = dataType;
		this.creationTime = creationTime;
		this.valid = true;
	}

	
}
