package com.dinglian.server.chuqulang.dao;

import java.util.List;

import com.dinglian.server.chuqulang.base.SearchCriteria;
import com.dinglian.server.chuqulang.model.UserAttention;

public interface GeneralDao {

	List<UserAttention> getUserAttentions(SearchCriteria searchCriteria);

	int getUserAttentionTotalCount(SearchCriteria searchCriteria);

	void saveUserAttention(UserAttention attention);

}
