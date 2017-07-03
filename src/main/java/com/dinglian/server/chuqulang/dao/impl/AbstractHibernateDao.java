package com.dinglian.server.chuqulang.dao.impl;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractHibernateDao<E extends Serializable> {
	private Class<E> entityClass;

	@Autowired
	private SessionFactory sessionFactory;

	protected AbstractHibernateDao(Class<E> entityClass) {
		this.entityClass = entityClass;
	}

	public Session getCurrentSession() {
		return sessionFactory.getCurrentSession();
	}

	public E findById(Serializable id) {
		return (E) getCurrentSession().get(entityClass, id);
	}
	
	public E load(Serializable id) {
		return (E) getCurrentSession().load(entityClass, id);
	}

	public void save(E e) {
		getCurrentSession().save(e);
	}
	
	public void saveOrUpdate(E e){
		getCurrentSession().saveOrUpdate(e);
	}

	public void delete(E e) {
		getCurrentSession().delete(e);
	}

	public List<E> query(String hql, Map<String, Object> map, Object... args) {
		Query query = getCurrentSession().createQuery(hql);
		if (args != null) {
			for (int i = 0; i < args.length; i++) {
				query.setParameter(i, args[i]);
			}
		}
		if (map != null && map.containsKey("pageNo") && map.containsKey("pageSize")) {
			Integer pageSize = (Integer) map.get("pageSize");
			Integer pageNo = (Integer) map.get("pageNo");
			query.setMaxResults(pageSize);
			query.setFirstResult((pageNo - 1) * pageSize);
		}
		return query.list();
	}

	public Class<E> getEntityClass() {
		return entityClass;
	}

	public void setEntityClass(Class<E> entityClass) {
		this.entityClass = entityClass;
	}

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

}
