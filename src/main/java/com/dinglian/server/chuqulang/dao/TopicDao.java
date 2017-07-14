package com.dinglian.server.chuqulang.dao;

import java.util.List;

import com.dinglian.server.chuqulang.model.Topic;

public interface TopicDao extends GenericDao<Topic> {

	List<Topic> getTopicsByName(String keyword);

}