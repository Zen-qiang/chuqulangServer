package com.dinglian.server.chuqulang.dao.impl;

import java.util.Date;
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
		/*if (searchCriteria.getCategory() != null) {
			sb.append("left outer join fetch e.tags tags ");
		}*/
		sb.append("WHERE 1=1 AND e.status != '0' AND e.endTime > :endTime ");
		if (StringUtils.isNotBlank(searchCriteria.getKeyword())) {
			sb.append("AND e.name like :keyword ");
		}
		// in 标签数组
		/*if (searchCriteria.getCategory() != null) {
			sb.append("AND tags.tag.id = :category ");
		}*/
		
		// 开始时间升序排列
		String orderBy = "ORDER BY e.startTime DESC ";
		
		sb.append(orderBy);
		
		Query query = getCurrentSession().createQuery(sb.toString());
		query.setTimestamp("endTime", new Date());
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
	public int getActivityTotalCount() {
		Query query = getCurrentSession().createQuery("SELECT COUNT(e) FROM Event e ");
		int count = ((Number)query.uniqueResult()).intValue();
		return count;
	}

	@Override
	public int getUserActivityTotalCount(int userId) {
		String sql = "SELECT COUNT(1) FROM event_user WHERE fk_user_id = :userId";
		Query query = getCurrentSession().createSQLQuery(sql).setInteger("userId", userId);
		int count = ((Number)query.uniqueResult()).intValue();
		return count;
	}

	@Override
	public List<Event> getUserActivityList(SearchCriteria searchCriteria) {
		String sql = "SELECT e.* FROM EVENT e LEFT JOIN event_user eu ON e.id = eu.fk_event_id WHERE 1=1 ";
		if (searchCriteria.getDataType().equals(Event.DATATYPE_ALL)) {
			sql += "AND eu.fk_user_id = :userId AND eu.effective = 1 ";
		} else if (searchCriteria.getDataType().equals(Event.DATATYPE_RELEASE)) {
			sql += "AND e.fk_user_id = :userId AND e.status != '0' ";
		} else if (searchCriteria.getDataType().equals(Event.DATATYPE_JOIN)) {
			sql += "AND eu.fk_user_id = :userId AND e.fk_user_id != :userId AND eu.effective = 1 AND e.status != '0' ";
		} else if (searchCriteria.getDataType().equals(Event.DATATYPE_EXPIRE)) {
			sql += "AND eu.fk_user_id = :userId AND e.status = '0' ";
		}
		sql += "ORDER BY e.start_time DESC";
		Query query = getCurrentSession().createSQLQuery(sql).addEntity(Event.class);
		query.setInteger("userId", searchCriteria.getUserId());
		if (searchCriteria.getPageSize() != 0) {
			query.setFirstResult(searchCriteria.getStartRow());
			query.setMaxResults(searchCriteria.getPageSize());
		}
		return query.list();
	}

}
