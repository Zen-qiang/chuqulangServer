package com.dinglian.server.chuqulang.service;

import java.util.List;
import java.util.Map;

import com.dinglian.server.chuqulang.base.SearchCriteria;
import com.dinglian.server.chuqulang.model.Event;
import com.dinglian.server.chuqulang.model.EventTag;
import com.dinglian.server.chuqulang.model.EventUser;
import com.dinglian.server.chuqulang.model.Tag;
import com.dinglian.server.chuqulang.model.UserCollect;

public interface ActivityService {

	void saveEvent(Event event) throws Exception;

	Event getEventById(int eventId) throws Exception;

	void saveEventUser(EventUser eventUser) throws Exception;

	Event findEventById(int id) throws Exception;

	Tag findTagById(int tagId) throws Exception;

	UserCollect getUserCollectByEventAndUser(int eventId, int userId) throws Exception;

	void saveUserCollect(UserCollect userCollect) throws Exception;

	void deleteUserCollect(UserCollect userCollect) throws Exception;

	void refresh(Event event);

	Map<String, Object> getActivityList(SearchCriteria searchCriteria);

	boolean checkFriendJoin(int id, int userId);

	Map<String, Object> getUserActivityList(SearchCriteria searchCriteria);

	List<Tag> getTags(String tagType);

	List<Tag> getSecondLevelTags(Integer parentId);

	Tag findTagByName(String tagUnlimited);

}
