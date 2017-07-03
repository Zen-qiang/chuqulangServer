package com.dinglian.server.chuqulang.dao;

import com.dinglian.server.chuqulang.model.UserCollect;

public interface UserCollectDao extends GenericDao<UserCollect> {

	UserCollect getUserCollectByEventAndUser(int eventId, int userId);

}