package com.dinglian.server.chuqulang.dao.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.dinglian.server.chuqulang.base.SearchCriteria;
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
	public List<Event> getActivityList(SearchCriteria searchCriteria) {
		StringBuffer sb = new StringBuffer("FROM Event e ");
		if (searchCriteria.getCategory() != null) {
			sb.append("left outer join fetch e.tags tags ");
		}
		sb.append("WHERE 1=1 ");
		if (StringUtils.isNotBlank(searchCriteria.getKeyword())) {
			sb.append("AND e.name like :keyword ");
		}
		if (searchCriteria.isOwnList()) {
			sb.append("AND e.creator.id = :userId ");
		}
		
		if (searchCriteria.getCategory() != null) {
			sb.append("AND tags.tag.id = :category ");
		}
		if (StringUtils.isNotBlank(searchCriteria.getStatus())) {
			if (searchCriteria.getStatus().equals(Event.STATUS_OVER)) {
				sb.append("AND e.status = '" + Event.STATUS_OVER + "' ");
			} else if (searchCriteria.getStatus().equals(Event.STATUS_PROGRESS)) {
				sb.append("AND e.status = '" + Event.STATUS_PROGRESS + "' ");
			} else if (searchCriteria.getStatus().equals(Event.STATUS_SIGNUP)) {
				sb.append("AND e.status = '" + Event.STATUS_SIGNUP + "' ");
			}
		}
		
		String orderBy = "ORDER BY e.creationDate DESC ";
		if (StringUtils.isNotBlank(searchCriteria.getOrderBy())) {
			if (Event.ORDERBY_DEFAULT.equals(searchCriteria.getOrderBy())) {
				orderBy = "ORDER BY e.creationDate DESC,e.cost ";
			} else if (Event.ORDERBY_CLOSEST.equals(searchCriteria.getOrderBy())) {
				orderBy = "";
			} else if (Event.ORDERBY_COST_ASC.equals(searchCriteria.getOrderBy())) {
				orderBy = "ORDER BY e.cost ";
			} else if (Event.ORDERBY_COST_DESC.equals(searchCriteria.getOrderBy())) {
				orderBy = "ORDER BY e.cost DESC ";
			}
		}
		sb.append(orderBy);
		
		Query query = getCurrentSession().createQuery(sb.toString());
		if (StringUtils.isNotBlank(searchCriteria.getKeyword())) {
			query.setString("keyword", "%" + searchCriteria.getKeyword() + "%");
		}
		if (searchCriteria.getCategory() != null) {
			query.setInteger("category", searchCriteria.getCategory());
		}
		if (searchCriteria.isOwnList()) {
			query.setInteger("userId", searchCriteria.getUserId());
		}
		
		if (searchCriteria.getPageSize() != 0) {
			query.setFirstResult(searchCriteria.getStartRow());
			query.setMaxResults(searchCriteria.getPageSize());
		}
		return query.list();
	}

	@Override
	public int getActivityTotalCount() {
		Query query = getCurrentSession().createQuery("SELECT COUNT(e) FROM Event e ");
		int count = ((Number)query.uniqueResult()).intValue();
		return count;
	}

}
