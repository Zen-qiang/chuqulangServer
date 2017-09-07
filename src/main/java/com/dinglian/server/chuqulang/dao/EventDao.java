package com.dinglian.server.chuqulang.dao;

import java.util.List;

import com.dinglian.server.chuqulang.base.SearchCriteria;
import com.dinglian.server.chuqulang.model.Event;

public interface EventDao extends GenericDao<Event> {

	List<Event> getActivityList(SearchCriteria searchCriteria);

	int getActivityTotalCount();

	int getUserActivityTotalCount(int userId);

	List<Event> getUserActivityList(SearchCriteria searchCriteria);

	List<Event> getSingnUpActivitys();

	void changeActivityStatus(int id, String status, String originStatus);

	int getActivityUserCount(int id);

}