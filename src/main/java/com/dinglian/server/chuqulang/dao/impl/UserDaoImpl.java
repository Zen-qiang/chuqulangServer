package com.dinglian.server.chuqulang.dao.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.dinglian.server.chuqulang.base.SearchCriteria;
import com.dinglian.server.chuqulang.dao.UserDao;
import com.dinglian.server.chuqulang.model.User;
import com.dinglian.server.chuqulang.model.UserCoterieSetting;

@Repository("userDao")
public class UserDaoImpl extends AbstractHibernateDao<User> implements UserDao {

	public UserDaoImpl() {
		super(User.class);
	}

	protected UserDaoImpl(Class<User> entityClass) {
		super(entityClass);
	}

	@Override
	public void register(User user) {
		this.getCurrentSession().save(user);
	}

	@Override
	public User getUser(SearchCriteria searchCriteria) {
		String hql = "FROM User u WHERE 1=1 ";
		if (StringUtils.isNotBlank(searchCriteria.getPhoneNo())) {
			hql += " AND phoneNo = '" + searchCriteria.getPhoneNo() + "'";
		}
		return (User) this.getCurrentSession().createQuery(hql).uniqueResult();
	}

	@Override
	public User login(String userName, String password) {
		String hql = "FROM User u WHERE 1=1 AND u.userName = :userName AND u.password = :password ";
		return (User) this.getCurrentSession().createQuery(hql).setString("userName", userName)
				.setString("password", password).uniqueResult();
	}

	@Override
	public User getUserByUsername(String userName) {
		String hql = "FROM User u WHERE 1=1 AND userName = :userName ";
		return (User) this.getCurrentSession().createQuery(hql).setString("userName", userName).uniqueResult();
	}

	@Override
	public User getUserByAccid(String faccid) {
		String hql = "FROM User u WHERE 1=1 AND accid = :accid ";
		return (User) this.getCurrentSession().createQuery(hql).setString("accid", faccid).uniqueResult();
	}

	@Override
	public User getUserByOpenId(String openId) {
		String hql = "FROM User u WHERE 1=1 AND openId = :openId ";
		return (User) this.getCurrentSession().createQuery(hql).setString("openId", openId).uniqueResult();
	}

	@Override
	public UserCoterieSetting findUserCoterieSetting(int userId, int coterieId) {
		String hql = "FROM UserCoterieSetting WHERE user.id = :userId AND coterie.id = :coterieId ";
		return (UserCoterieSetting) getCurrentSession().createQuery(hql)
				.setInteger("userId", userId).setInteger("coterieId", coterieId).uniqueResult();
	}

	@Override
	public void saveUserCoterieSetting(UserCoterieSetting userCoterieSetting) {
		getCurrentSession().save(userCoterieSetting);
	}

}
