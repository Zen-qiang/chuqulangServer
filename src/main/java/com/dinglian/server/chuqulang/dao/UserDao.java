package com.dinglian.server.chuqulang.dao;

import com.dinglian.server.chuqulang.base.SearchCriteria;
import com.dinglian.server.chuqulang.model.User;
import com.dinglian.server.chuqulang.model.UserCoterieSetting;

public interface UserDao extends GenericDao<User> {

	void register(User user);

	User getUser(SearchCriteria searchCriteria);

	User login(String userName, String password);

	User getUserByUsername(String username);

	User getUserByAccid(String faccid);

	User getUserByOpenId(String openId);

	UserCoterieSetting findUserCoterieSetting(int userId, int coterieId);

	void saveUserCoterieSetting(UserCoterieSetting userCoterieSetting);


}