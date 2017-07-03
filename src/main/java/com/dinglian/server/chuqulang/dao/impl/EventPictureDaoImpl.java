package com.dinglian.server.chuqulang.dao.impl;

import org.springframework.stereotype.Repository;

import com.dinglian.server.chuqulang.dao.EventPictureDao;
import com.dinglian.server.chuqulang.model.EventPicture;

@Repository("eventPictureDao")
public class EventPictureDaoImpl extends AbstractHibernateDao<EventPicture> implements EventPictureDao {

	public EventPictureDaoImpl() {
		super(EventPicture.class);
	}

	protected EventPictureDaoImpl(Class<EventPicture> entityClass) {
		super(entityClass);
	}

}
