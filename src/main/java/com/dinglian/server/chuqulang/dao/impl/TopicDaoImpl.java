package com.dinglian.server.chuqulang.dao.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
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

	@Override
	public List<Topic> getTopicsByName(String keyword) {
		if (StringUtils.isNotBlank(keyword)) {
			String hql = "FROM Topic WHERE 1=1 AND description like :keyword ORDER BY creationDate DESC ";
			Query query = getCurrentSession().createQuery(hql);
			query.setString("keyword", "%" + keyword + "%");
			return query.list();
		}
		return null;
	}

}
