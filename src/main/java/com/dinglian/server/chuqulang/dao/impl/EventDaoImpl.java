package com.dinglian.server.chuqulang.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.dinglian.server.chuqulang.dao.EventDao;
import com.dinglian.server.chuqulang.model.Event;

@Repository("eventDao")
public class EventDaoImpl extends AbstractHibernateDao<Event> implements EventDao {

	public EventDaoImpl() {
		super(Event.class);
	}

	protected EventDaoImpl(Class<Event> entityClass) {
		super(entityClass);
	}

	@Override
	public List<Event> getAllActivity() {
		return getCurrentSession().createQuery("FROM Event").list();
	}

}
