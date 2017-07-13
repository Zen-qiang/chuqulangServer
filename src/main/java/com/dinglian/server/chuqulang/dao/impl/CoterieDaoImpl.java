package com.dinglian.server.chuqulang.dao.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.dinglian.server.chuqulang.base.SearchCriteria;
import com.dinglian.server.chuqulang.dao.CoterieDao;
import com.dinglian.server.chuqulang.model.Coterie;
import com.dinglian.server.chuqulang.model.Topic;

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

	@Override
	public int getCoterieTotalCount() {
		Query query = getCurrentSession().createQuery("SELECT COUNT(c) FROM Coterie c ");
		int count = ((Number)query.uniqueResult()).intValue();
		return count;
	}

	@Override
	public List<Coterie> getCoterieList(SearchCriteria searchCriteria) {
		String hql = "FROM Coterie WHERE 1=1 ";
		if (StringUtils.isNotBlank(searchCriteria.getTypeName())) {
			hql += "AND tag.typeName.name = :typeName ";
		}
		if (searchCriteria.getTagId() != null) {
			hql += "AND tag.id = :tagId ";
		}
		String orderBy = "ORDER BY creationDate DESC";
		if (StringUtils.isNotBlank(searchCriteria.getOrderBy()) && searchCriteria.getOrderBy().equalsIgnoreCase(Coterie.TYPE_HOT)) {
			orderBy = "ORDER BY hot DESC";
		}
		hql += orderBy;
		Query query = getCurrentSession().createQuery(hql);
		if (StringUtils.isNotBlank(searchCriteria.getTypeName())) {
			query.setString("typeName", searchCriteria.getTypeName());
		}
		if (searchCriteria.getTagId() != null) {
			query.setInteger("tagId", searchCriteria.getTagId());
		}
		if (searchCriteria.getPageSize() != 0) {
			query.setFirstResult(searchCriteria.getStartRow());
			query.setMaxResults(searchCriteria.getPageSize());
		}
		return query.list();
	}

	@Override
	public int getTopicTotalCount(Integer coterieId) {
		Query query = getCurrentSession().createQuery("SELECT COUNT(c) FROM Topic c WHERE c.coterie.id = :coterieId ");
		query.setInteger("coterieId", coterieId);
		int count = ((Number)query.uniqueResult()).intValue();
		return count;
	}

	@Override
	public List<Topic> getTopicList(SearchCriteria searchCriteria) {
		String hql = "FROM Topic WHERE coterie.id = :coterieId ";
		String orderBy = "ORDER BY creationDate DESC ";
		if (StringUtils.isNotBlank(searchCriteria.getOrderBy())) {
			// 时间顺序倒序
			// 热门顺序倒序
		}
		hql += orderBy;
		Query query = getCurrentSession().createQuery(hql);
		query.setInteger("coterieId", searchCriteria.getCoterieId());
		if (searchCriteria.getPageSize() != 0) {
			query.setFirstResult(searchCriteria.getStartRow());
			query.setMaxResults(searchCriteria.getPageSize());
		}
		return query.list();
	}

}
