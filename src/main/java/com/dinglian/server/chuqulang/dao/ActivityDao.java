package com.dinglian.server.chuqulang.dao;

import com.dinglian.server.chuqulang.model.Event;

public interface ActivityDao {

	void refresh(Event event);

	boolean checkFriendJoin(int eventId, int userId);

}
