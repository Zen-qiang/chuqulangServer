package com.dinglian.server.chuqulang.dao.impl;

import org.springframework.stereotype.Repository;

import com.dinglian.server.chuqulang.dao.ContactDao;
import com.dinglian.server.chuqulang.model.Contact;

@Repository
public class ContactDaoImpl extends AbstractHibernateDao<Contact> implements ContactDao {

	public ContactDaoImpl() {
		super(Contact.class);
	}

	protected ContactDaoImpl(Class<Contact> entityClass) {
		super(entityClass);
	}


}
