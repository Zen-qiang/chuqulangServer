package com.dinglian.server.chuqulang.dao;

import java.util.List;

import com.dinglian.server.chuqulang.base.SearchCriteria;
import com.dinglian.server.chuqulang.model.CoterieGuy;
import com.dinglian.server.chuqulang.model.TopicPraise;
import com.dinglian.server.chuqulang.model.UserAttention;

public interface GeneralDao {

	List<UserAttention> getUserAttentions(SearchCriteria searchCriteria);

	int getUserAttentionTotalCount(SearchCriteria searchCriteria);

	void saveUserAttention(UserAttention attention);

	void saveTopicPraise(TopicPraise topicPraise);

	void saveCoterieGuy(CoterieGuy coterieGuy);

	void deleteCoterieGuy(int coterieId, int userId);

}
