package com.dinglian.server.chuqulang.dao.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
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
	public List<Event> getAllActivity(String keyword) {
		String hql = "FROM Event WHERE 1=1 ";
		if (StringUtils.isNotBlank(keyword)) {
			hql += "AND name like :keyword ";
		}
		Query query = getCurrentSession().createQuery(hql);
		if (StringUtils.isNotBlank(keyword)) {
			query.setString("keyword", "%" + keyword + "%");
		}
		return query.list();
	}

}
