package com.dinglian.server.chuqulang.service;

import java.util.List;

import com.dinglian.server.chuqulang.model.Coterie;
import com.dinglian.server.chuqulang.model.Topic;

public interface DiscoverService {

	List<Coterie> getCoterieList(int tagId, String type) throws Exception;

	Coterie findCoterieById(int coterieId) throws Exception;

	void saveTopic(Topic topic) throws Exception;

	Topic findTopicById(int topicId) throws Exception;

}
