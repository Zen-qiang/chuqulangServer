package com.dinglian.server.chuqulang.dao.impl;

import org.springframework.stereotype.Repository;

import com.dinglian.server.chuqulang.dao.UserCollectDao;
import com.dinglian.server.chuqulang.model.UserCollect;

@Repository
public class UserCollectDaoImpl extends AbstractHibernateDao<UserCollect> implements UserCollectDao {

	public UserCollectDaoImpl() {
		super(UserCollect.class);
	}

	protected UserCollectDaoImpl(Class<UserCollect> entityClass) {
		super(entityClass);
	}

	@Override
	public UserCollect getUserCollectByEventAndUser(int eventId, int userId) {
		String hql = "FROM UserCollect WHERE event.id = :eventId AND user.id = :userId ";
		return (UserCollect) getCurrentSession().createQuery(hql).setInteger("eventId", eventId).setInteger("userId", userId).uniqueResult();
	}

}
