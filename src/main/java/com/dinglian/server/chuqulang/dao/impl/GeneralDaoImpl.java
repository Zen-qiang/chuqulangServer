package com.dinglian.server.chuqulang.dao.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.dinglian.server.chuqulang.base.SearchCriteria;
import com.dinglian.server.chuqulang.dao.GeneralDao;
import com.dinglian.server.chuqulang.model.Contact;
import com.dinglian.server.chuqulang.model.CoterieGuy;
import com.dinglian.server.chuqulang.model.TopicPraise;
import com.dinglian.server.chuqulang.model.UserAttention;

@Repository
public class GeneralDaoImpl implements GeneralDao {

	@Autowired
	private SessionFactory sessionFactory;

	private Session getCurrentSession() {
		return sessionFactory.getCurrentSession();
	}

	@Override
	public List<UserAttention> getUserAttentions(SearchCriteria searchCriteria) {
		String hql = "FROM UserAttention WHERE 1=1 ";
		if (searchCriteria.isAttention()) {
			hql += "AND user.id = :userId ";
		} else {
			hql += "AND attentionUser.id = :userId ";
		}
		String orderBy = "ORDER BY creationDate DESC ";
		if (StringUtils.isNotBlank(searchCriteria.getOrderBy())) {

		}
		hql += orderBy;
		Query query = getCurrentSession().createQuery(hql);
		query.setInteger("userId", searchCriteria.getUserId());

		if (searchCriteria.getPageSize() != 0) {
			query.setFirstResult(searchCriteria.getStartRow());
			query.setMaxResults(searchCriteria.getPageSize());
		}

		return query.list();
	}

	@Override
	public int getUserAttentionTotalCount(SearchCriteria searchCriteria) {
		String hql = "SELECT COUNT(e) FROM UserAttention e WHERE 1=1 ";
		if (searchCriteria.isAttention()) {
			hql += "AND e.user.id = :userId ";
		} else {
			hql += "AND e.attentionUser.id = :userId ";
		}
		Query query = getCurrentSession().createQuery(hql);
		query.setInteger("userId", searchCriteria.getUserId());
		int count = ((Number) query.uniqueResult()).intValue();
		return count;
	}

	@Override
	public void saveUserAttention(UserAttention attention) {
		getCurrentSession().save(attention);
	}

	@Override
	public void saveTopicPraise(TopicPraise topicPraise) {
		getCurrentSession().save(topicPraise);
	}

	@Override
	public void saveCoterieGuy(CoterieGuy coterieGuy) {
		getCurrentSession().save(coterieGuy);
	}

	@Override
	public void deleteCoterieGuy(int coterieId, int userId) {
		String hql = "DELETE FROM CoterieGuy WHERE coterie.id = :coterieId AND user.id = :userId ";
		getCurrentSession().createQuery(hql).setInteger("coterieId", coterieId).setInteger("userId", userId)
				.executeUpdate();

	}

	@Override
	public Contact getContact(int userId, int contactUserId) {
		String hql = "FROM Contact WHERE user.id = :userId AND contactUser.id = :contactUserId ";
		return (Contact) getCurrentSession().createQuery(hql).setInteger("userId", userId)
				.setInteger("contactUserId", contactUserId).uniqueResult();
	}

	@Override
	public void saveContact(Contact contact) {
		getCurrentSession().save(contact);
	}

	@Override
	public void deleteContact(int userId, int contactUserId) {
		String hql = "DELETE FROM Contact WHERE user.id = :userId AND contactUser.id = :contactUserId ";
		getCurrentSession().createQuery(hql).setInteger("userId", userId).setInteger("contactUserId", contactUserId)
				.executeUpdate();
	}

}
