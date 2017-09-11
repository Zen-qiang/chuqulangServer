package com.dinglian.server.chuqulang.dao.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.dinglian.server.chuqulang.base.SearchCriteria;
import com.dinglian.server.chuqulang.dao.CoterieDao;
import com.dinglian.server.chuqulang.model.Coterie;
import com.dinglian.server.chuqulang.model.Event;
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
	public int getCoterieTotalCount() {
		Query query = getCurrentSession().createQuery("SELECT COUNT(c) FROM Coterie c ");
		int count = ((Number)query.uniqueResult()).intValue();
		return count;
	}

	@Override
	public List<Coterie> getCoterieList(SearchCriteria searchCriteria) {
		String hql = "SELECT distinct c FROM Coterie c LEFT JOIN c.tags tag WHERE 1=1 AND c.status = :status ";
		if (searchCriteria.getTags() != null && searchCriteria.getTags().size() > 0) {
			hql += "AND tag.tag.id IN (:tagList) ";
		}
		if (StringUtils.isNotBlank(searchCriteria.getKeyword())) {
			hql += "AND c.name like :keyword ";
		}
		String orderBy = "ORDER BY c.creationDate DESC";
		if (StringUtils.isNotBlank(searchCriteria.getOrderBy()) && searchCriteria.getOrderBy().equalsIgnoreCase(Coterie.TYPE_HOT)) {
			orderBy = "ORDER BY c.hot DESC";
		}
		hql += orderBy;
		Query query = getCurrentSession().createQuery(hql);
		query.setInteger("status", Coterie.STATUS_NORMAL);
		
		if (searchCriteria.getTags() != null && searchCriteria.getTags().size() > 0) {
			query.setParameterList("tagList", searchCriteria.getTags());
		}
		if (StringUtils.isNotBlank(searchCriteria.getKeyword())) {
			query.setString("keyword", "%" + searchCriteria.getKeyword() + "%");
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
		if (searchCriteria.getTopicDataType() != null) {
			hql += "AND topicType = :topicType ";
		}
		if (StringUtils.isNotBlank(searchCriteria.getDataType())) {
			if (searchCriteria.getDataType().equals(Topic.DATATYPE_HISTROY)) {
				hql += "AND event.status = '" + Event.STATUS_OVER + "' ";
			}
		}
		String orderBy = "ORDER BY creationDate DESC ";
		if (StringUtils.isNotBlank(searchCriteria.getOrderBy())) {
			if (searchCriteria.getOrderBy().equals(Event.ORDER_BY_START_TIME)) {
				orderBy = "ORDER BY event.startTime ";
			}
		}
		hql += orderBy;
		Query query = getCurrentSession().createQuery(hql);
		query.setInteger("coterieId", searchCriteria.getCoterieId());
		if (searchCriteria.getTopicDataType() != null) {
			query.setInteger("topicType", searchCriteria.getTopicDataType());
		}
		if (searchCriteria.getPageSize() != 0) {
			query.setFirstResult(searchCriteria.getStartRow());
			query.setMaxResults(searchCriteria.getPageSize());
		}
		return query.list();
	}

	@Override
	public List<Coterie> getCoteriesByName(String keyword) {
		if (StringUtils.isNotBlank(keyword)) {
			String hql = "FROM Coterie WHERE 1=1 AND (name like :keyword OR description like :keyword) ORDER BY hot DESC ";
			Query query = getCurrentSession().createQuery(hql);
			query.setString("keyword", "%" + keyword + "%");
			return query.list();
		}
		return null;
	}

	@Override
	public List<Coterie> getMyCoteries(String dataType, int userId) {
		String hql = "SELECT distinct c FROM Coterie c LEFT JOIN FETCH c.coterieGuys guy WHERE 1=1 AND c.status = :status ";
		if (StringUtils.isNotBlank(dataType)) {
			if (dataType.equalsIgnoreCase(Coterie.DATATYPE_CREATED)) {
				hql += "AND c.creator.id = :userId ";
			} else if (dataType.equalsIgnoreCase(Coterie.DATATYPE_ATTENTION)) {
				hql += "AND c.creator.id != :userId AND guy.user.id = :userId ";
			} else {
				hql += "AND guy.user.id = :userId ";
			}
		} else {
			hql += "AND guy.user.id = :userId ";
		}
		hql += "ORDER BY guy.creationDate DESC ";
		return getCurrentSession().createQuery(hql).setInteger("status", Coterie.STATUS_NORMAL).setInteger("userId", userId).list();
	}

	@Override
	public Coterie getLastCoterie(int userId) {
		String sql = "SELECT c.* FROM coterie c JOIN EVENT e ON c.id = e.fk_coterie_id JOIN event_user eu ON e.id = eu.fk_event_id ";
		sql += "WHERE eu.fk_user_id = :userId ORDER BY eu.creation_date DESC LIMIT 1 ";
		return (Coterie) getCurrentSession().createSQLQuery(sql).addEntity(Coterie.class).setInteger("userId", userId).uniqueResult();
	}

	@Override
	public boolean hasActivityProcess(int coterieId) {
		Query query = getCurrentSession().createSQLQuery("SELECT COUNT(1) FROM event e WHERE e.fk_coterie_id = :coterieId AND (e.status = :status1 OR e.status = :status2) ");
		query.setInteger("coterieId", coterieId);
		query.setString("status1", Event.STATUS_PROCESS);
		query.setString("status2", Event.STATUS_SIGNUP);
		int count = ((Number)query.uniqueResult()).intValue();
		if (count > 0) {
			return true;
		}
		return false;
	}

}
