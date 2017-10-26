package com.dinglian.server.chuqulang.dao.impl;

import java.util.Calendar;
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
import com.dinglian.server.chuqulang.model.Coterie;
import com.dinglian.server.chuqulang.model.CoterieCarouselPicture;
import com.dinglian.server.chuqulang.model.CoterieGuy;
import com.dinglian.server.chuqulang.model.Event;
import com.dinglian.server.chuqulang.model.NewsMaterial;
import com.dinglian.server.chuqulang.model.SensitiveWord;
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
		return (Tag) getCurrentSession().createQuery("FROM Tag WHERE name = :name").setString("name", tagName)
				.uniqueResult();
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
		int count = ((Number) query.uniqueResult()).intValue();
		return count;
	}

	@Override
	public Event findEventById(int id) {
		return (Event) getCurrentSession().get(Event.class, id);
	}

	@Override
	public List<Event> getSingnUpActivitys() {
		String hql = "FROM Event WHERE (status = :status1 OR status = :status2) AND (startTime >= :date1 OR endTime >= :date2) ";
		return getCurrentSession().createQuery(hql).setString("status1", Event.STATUS_SIGNUP)
				.setString("status2", Event.STATUS_PROCESS).setTimestamp("date1", new Date())
				.setTimestamp("date2", new Date()).list();
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

	@Override
	public List<SensitiveWord> loadAllSensitiveWord() {
		String hql = "FROM SensitiveWord ";
		return getCurrentSession().createQuery(hql).list();
	}

	@Override
	public Coterie getCoterieByActivityId(int activityId) {
		String sql = "SELECT c.* FROM coterie c JOIN EVENT e ON e.fk_coterie_id = c.id WHERE e.id = :activityId ";
		return (Coterie) getCurrentSession().createSQLQuery(sql).addEntity(Coterie.class)
				.setInteger("activityId", activityId).uniqueResult();
	}

	@Override
	public void changeCoterieStatus(int coterieId, int status) {
		String sql = "update coterie set status = :status where id = :id ";
		getCurrentSession().createSQLQuery(sql).setInteger("status", status).setInteger("id", coterieId)
				.executeUpdate();
	}

	@Override
	public List<Event> getTodayAllActivitys() {
		String sql = "SELECT * FROM event WHERE (status = :status1 OR status = :status2) AND ((start_time >= DATE_ADD(NOW(),INTERVAL -5 MINUTE) AND start_time < DATE_ADD(NOW(),INTERVAL 5 MINUTE)) OR (end_time >= DATE_ADD(NOW(),INTERVAL -5 MINUTE) AND end_time < DATE_ADD(NOW(),INTERVAL 5 MINUTE))) ";
		return getCurrentSession().createSQLQuery(sql).addEntity(Event.class).setString("status1", Event.STATUS_SIGNUP)
				.setString("status2", Event.STATUS_PROCESS).list();
	}

	@Override
	public void removeAllNewsMaterial() {
		String sql = "TRUNCATE TABLE `news_material`";
		getCurrentSession().createSQLQuery(sql).executeUpdate();
	}

	@Override
	public void updateNewsMaterial(List<NewsMaterial> materialList) {
		StringBuffer sb = new StringBuffer("INSERT INTO `news_material` (`media_id`, `digest`, `thumb_url`, `title`, `url`) VALUES ");
		
		for (int i = 0; i < materialList.size(); i++) {
			NewsMaterial newsMaterial = materialList.get(i);
			sb.append("(");
			sb.append("'" + newsMaterial.getMediaId() + "', ");
			sb.append("'" + newsMaterial.getDigest() + "', ");
			sb.append("'" + newsMaterial.getThumbUrl() + "', ");
			sb.append("'" + newsMaterial.getTitle() + "', ");
			sb.append("'" + newsMaterial.getUrl() + "'");
			sb.append(")");
			if (i != materialList.size() - 1) {
				sb.append(", ");
			}
		}
		
		getCurrentSession().createSQLQuery(sb.toString()).executeUpdate();
		
	}

}
