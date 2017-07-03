package com.dinglian.server.chuqulang.dao.impl;

import org.springframework.stereotype.Repository;

import com.dinglian.server.chuqulang.dao.EventTagDao;
import com.dinglian.server.chuqulang.model.EventTag;
import com.dinglian.server.chuqulang.model.Tag;

import java.util.List;

@Repository("eventTagDao")
public class EventTagDaoImpl extends AbstractHibernateDao<EventTag> implements EventTagDao {

	public EventTagDaoImpl() {
		super(EventTag.class);
	}

	protected EventTagDaoImpl(Class<EventTag> entityClass) {
		super(entityClass);
	}

}
