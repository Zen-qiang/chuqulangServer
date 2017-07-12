package com.dinglian.server.chuqulang.dao.impl;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.dinglian.server.chuqulang.dao.ActivityDao;
import com.dinglian.server.chuqulang.model.Event;

@Repository
public class ActivityDaoImpl implements ActivityDao {
	
	@Autowired
	private SessionFactory sessionFactory;
	
	private Session getCurrentSession() {
		return sessionFactory.getCurrentSession();
	}

	@Override
	public void refresh(Event event) {
		getCurrentSession().refresh(event);
	}

	@Override
	public boolean checkFriendJoin(int eventId, int userId) {
		String sql = "SELECT COUNT(1) FROM event_user eu JOIN contact c ON eu.fk_user_id = c.contact_user_id WHERE eu.fk_event_id = :eventId AND c.fk_user_id = :userId ";
		Object result = getCurrentSession().createSQLQuery(sql).setInteger("eventId", eventId).setInteger("userId", userId).uniqueResult();
		if (result != null) {
			int count = Integer.parseInt(result.toString());
			if (count > 0) {
				return true;
			}
		}
		return false;
	}

}
