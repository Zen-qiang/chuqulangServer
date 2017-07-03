package com.dinglian.server.chuqulang.dao.impl;

import org.springframework.stereotype.Repository;

import com.dinglian.server.chuqulang.dao.TopicDao;
import com.dinglian.server.chuqulang.model.Topic;

@Repository
public class TopicDaoImpl extends AbstractHibernateDao<Topic> implements TopicDao {

	public TopicDaoImpl() {
		super(Topic.class);
	}

	protected TopicDaoImpl(Class<Topic> entityClass) {
		super(entityClass);
	}

}
