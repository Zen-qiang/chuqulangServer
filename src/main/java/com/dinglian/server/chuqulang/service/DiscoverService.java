package com.dinglian.server.chuqulang.service;

import java.util.Map;

import com.dinglian.server.chuqulang.base.SearchCriteria;
import com.dinglian.server.chuqulang.model.Coterie;
import com.dinglian.server.chuqulang.model.CoterieGuy;
import com.dinglian.server.chuqulang.model.Topic;
import com.dinglian.server.chuqulang.model.TopicPraise;

public interface DiscoverService {

	Coterie findCoterieById(int coterieId) throws Exception;

	void saveTopic(Topic topic) throws Exception;

	Topic findTopicById(int topicId) throws Exception;

	Map<String, Object> getCoterieList(SearchCriteria searchCriteria);

	Map<String, Object> getTopicList(SearchCriteria searchCriteria);

	Map<String, Object> searchActivityOrTopic(String keyword);

	void saveTopicPraise(TopicPraise topicPraise);

	void saveCoterieGuy(CoterieGuy coterieGuy);

}
