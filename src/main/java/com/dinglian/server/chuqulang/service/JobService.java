package com.dinglian.server.chuqulang.service;

import java.util.List;

import com.dinglian.server.chuqulang.model.Event;
import com.dinglian.server.chuqulang.model.SensitiveWord;
import com.dinglian.server.chuqulang.model.User;

public interface JobService {

	int getActivityUserCount(int activityId);

	void changeActivityStatus(int activityId, String statusOver);

	String getWxAccessToken();

	Event findEventById(int activityId);
	
	List<Event> getSingnUpActivitys();

	User getActivityCreator(int activityId);

	List<User> getActivityMembers(int activityId);

	List<SensitiveWord> loadAllSensitiveWord();

}
