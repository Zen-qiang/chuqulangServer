package com.dinglian.server.chuqulang.dao.impl;

import java.util.Date;
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
import com.dinglian.server.chuqulang.model.CoterieCarouselPicture;
import com.dinglian.server.chuqulang.model.CoterieGuy;
import com.dinglian.server.chuqulang.model.Event;
import com.dinglian.server.chuqulang.model.Tag;
import com.dinglian.server.chuqulang.model.TopicPraise;
import com.dinglian.server.chuqulang.model.User;
import com.dinglian.server.chuqulang.model.UserAttention;
import com.dinglian.server.chuqulang.model.WxAccessToken;
import com.dinglian.server.chuqulang.model.WxJsApiTicket;
import com.dinglian.server.chuqulang.model.WxOAuth2AccessToken;

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

	@Override
	public WxAccessToken findWxAccessTokenById(int accessTokenId) {
		return (WxAccessToken) getCurrentSession().get(WxAccessToken.class, accessTokenId);
	}

	@Override
	public void saveWxAccessToken(WxAccessToken token) {
		getCurrentSession().save(token);
	}

	@Override
	public WxOAuth2AccessToken findWxOAuth2AccessTokenByOpenId(String openId) {
		String hql = "FROM WxOAuth2AccessToken WHERE openId = :openId ";
		return (WxOAuth2AccessToken) getCurrentSession().createQuery(hql).setString("openId", openId).uniqueResult();
	}

	@Override
	public void saveWxOAuth2AccessToken(WxOAuth2AccessToken token) {
		getCurrentSession().save(token);
	}

	@Override
	public List<CoterieCarouselPicture> getCoterieCarouselPictures() {
		return getCurrentSession().createQuery("FROM CoterieCarouselPicture ORDER BY orderNo").list();
	}

	@Override
	public Tag findTagByName(String tagName) {
		return (Tag) getCurrentSession().createQuery("FROM Tag WHERE name = :name").setString("name", tagName).uniqueResult();
	}

	@Override
	public WxJsApiTicket findWxJsApiTicketById(int jsapiTicketId) {
		return (WxJsApiTicket) getCurrentSession().get(WxJsApiTicket.class, jsapiTicketId);
	}

	@Override
	public void saveWxJsApiTicket(WxJsApiTicket jsApiTicket) {
		getCurrentSession().save(jsApiTicket);
	}
	
	@Override
	public void changeActivityStatus(int id, String status, String originStatus) {
		String sql = "UPDATE event SET status = :status WHERE id = :id ";
		if (StringUtils.isNotBlank(originStatus)) {
			sql += "AND status = :origin ";
		}
		Query query = getCurrentSession().createSQLQuery(sql);
		query.setString("status", status).setInteger("id", id);
		if (StringUtils.isNotBlank(originStatus)) {
			query.setString("origin", originStatus);
		}
		query.executeUpdate();
	}

	@Override
	public int getActivityUserCount(int id) {
		String sql = "SELECT COUNT(1) FROM event_user WHERE fk_event_id = :id AND effective = 1 ";
		Query query = getCurrentSession().createSQLQuery(sql).setInteger("id", id);
		int count = ((Number)query.uniqueResult()).intValue();
		return count;
	}

	@Override
	public Event findEventById(int id) {
		return (Event) getCurrentSession().get(Event.class, id);
	}

	@Override
	public List<Event> getSingnUpActivitys() {
		String hql = "FROM Event WHERE status = :status AND startTime >= :date ";
		return getCurrentSession().createQuery(hql).setString("status", Event.STATUS_SIGNUP).setTimestamp("date", new Date()).list();
	}

	@Override
	public User getActivityCreator(int id) {
		String sql = "SELECT u.* FROM user u JOIN event e ON u.id = e.fk_user_id WHERE e.id = :id";
		return (User) getCurrentSession().createSQLQuery(sql).addEntity(User.class).setInteger("id", id).uniqueResult();
	}

	@Override
	public List<User> getActivityMembers(int activityId) {
		String sql = "SELECT u.* FROM USER u JOIN event_user eu ON u.id = eu.fk_user_id WHERE eu.effective = 1 AND eu.fk_event_id = :id ";
		return getCurrentSession().createSQLQuery(sql).addEntity(User.class).setInteger("id", activityId).list();
	}

}
