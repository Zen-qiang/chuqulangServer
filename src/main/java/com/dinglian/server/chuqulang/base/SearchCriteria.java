package com.dinglian.server.chuqulang.base;

public class SearchCriteria {

	// User
	private int userId;
	private String phoneNo;

	// Event
	private String orderBy;
	private Integer category;
	private String status;
	private String keyword;
	private int startRow;
	private int pageSize;
	private boolean isOwnList;

	private String typeName;
	private Integer tagId;

	private int coterieId;
	private boolean isAttention;

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getPhoneNo() {
		return phoneNo;
	}

	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}

	public String getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	public Integer getCategory() {
		return category;
	}

	public void setCategory(Integer category) {
		this.category = category;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public int getStartRow() {
		return startRow;
	}

	public void setStartRow(int startRow) {
		this.startRow = startRow;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public boolean isOwnList() {
		return isOwnList;
	}

	public void setOwnList(boolean isOwnList) {
		this.isOwnList = isOwnList;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public Integer getTagId() {
		return tagId;
	}

	public void setTagId(Integer tagId) {
		this.tagId = tagId;
	}

	public int getCoterieId() {
		return coterieId;
	}

	public void setCoterieId(int coterieId) {
		this.coterieId = coterieId;
	}

	public boolean isAttention() {
		return isAttention;
	}

	public void setAttention(boolean isAttention) {
		this.isAttention = isAttention;
	}

}
