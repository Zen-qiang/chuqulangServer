package com.dinglian.server.chuqulang.dao.impl;

import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.dinglian.server.chuqulang.dao.CoterieDao;
import com.dinglian.server.chuqulang.model.Coterie;

@Repository
public class CoterieDaoImpl extends AbstractHibernateDao<Coterie> implements CoterieDao {

	public CoterieDaoImpl() {
		super(Coterie.class);
	}

	protected CoterieDaoImpl(Class<Coterie> entityClass) {
		super(entityClass);
	}

	@Override
	public List<Coterie> getCoterieList(int tagId, String type) {
		String hql = "FROM Coterie WHERE 1=1 ";
		if (tagId != 0) {
			hql += "AND tag.id = :tagId ";
		}
		if (type.equalsIgnoreCase(Coterie.TYPE_HOT)) {
			hql += "ORDER BY hot DESC ";
		} else if (type.equalsIgnoreCase(Coterie.TYPE_NEW)) {
			hql += "ORDER BY creationDate DESC ";
		}
		Query query = getCurrentSession().createQuery(hql);
		if (tagId != 0) {
			query.setInteger("tagId", tagId);
		}
		List<Coterie> list = query.list();
		return list;
	}

}
