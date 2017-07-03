package com.dinglian.server.chuqulang.dao.impl;

import org.springframework.stereotype.Repository;

import com.dinglian.server.chuqulang.dao.EventUserDao;
import com.dinglian.server.chuqulang.model.EventUser;

@Repository("eventUserDao")
public class EventUserDaoImpl extends AbstractHibernateDao<EventUser> implements EventUserDao {

	public EventUserDaoImpl() {
		super(EventUser.class);
	}

	protected EventUserDaoImpl(Class<EventUser> entityClass) {
		super(entityClass);
	}

}
