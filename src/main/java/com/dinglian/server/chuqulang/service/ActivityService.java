package com.dinglian.server.chuqulang.service;

import java.util.List;

import com.dinglian.server.chuqulang.model.Event;
import com.dinglian.server.chuqulang.model.EventPicture;
import com.dinglian.server.chuqulang.model.EventUser;
import com.dinglian.server.chuqulang.model.Tag;
import com.dinglian.server.chuqulang.model.TypeName;
import com.dinglian.server.chuqulang.model.UserCollect;

public interface ActivityService {

	TypeName getTypeNameByName(String typeNameStr) throws Exception;

	void saveEvent(Event event) throws Exception;

	void saveEventPicture(EventPicture eventPicture) throws Exception;

	Event getEventById(int eventId) throws Exception;

	void saveEventUser(EventUser eventUser) throws Exception;

	Event findEventById(int id) throws Exception;

	Tag findTagById(int tagId) throws Exception;

	UserCollect getUserCollectByEventAndUser(int eventId, int userId) throws Exception;

	void saveUserCollect(UserCollect userCollect) throws Exception;

	void deleteUserCollect(UserCollect userCollect) throws Exception;

	List<Event> getAllActivity(String keyword);

//	List<Tag> getTagListByTypeName(String typeName);

	List<TypeName> getActivityTypes(String type);

	List<Tag> getTagListByTypeNameId(Integer typeNameId);
}
