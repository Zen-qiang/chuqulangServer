package com.dinglian.server.chuqulang.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dinglian.server.chuqulang.dao.EventDao;
import com.dinglian.server.chuqulang.dao.EventPictureDao;
import com.dinglian.server.chuqulang.dao.EventTagDao;
import com.dinglian.server.chuqulang.dao.EventUserDao;
import com.dinglian.server.chuqulang.dao.TagDao;
import com.dinglian.server.chuqulang.dao.TypeNameDao;
import com.dinglian.server.chuqulang.dao.UserCollectDao;
import com.dinglian.server.chuqulang.model.Event;
import com.dinglian.server.chuqulang.model.EventPicture;
import com.dinglian.server.chuqulang.model.EventUser;
import com.dinglian.server.chuqulang.model.Tag;
import com.dinglian.server.chuqulang.model.TypeName;
import com.dinglian.server.chuqulang.model.UserCollect;
import com.dinglian.server.chuqulang.service.ActivityService;

@Service
public class ActivityServiceImpl implements ActivityService {

    @Autowired
    private TypeNameDao typeNameDao;

    @Autowired
    private EventDao eventDao;

    @Autowired
    private EventPictureDao eventPictureDao;

    @Autowired
    private EventUserDao eventUserDao;

    @Autowired
    private EventTagDao eventTagDao;
    
    @Autowired
    private TagDao tagDao;
    
    @Autowired
    private UserCollectDao userCollectDao;

    @Override
    public TypeName getTypeNameByName(String typeNameStr) throws Exception {
        return typeNameDao.getTypeNameByName(typeNameStr);
    }

    @Override
    public void saveEvent(Event event) throws Exception {
        eventDao.save(event);
    }

    @Override
    public void saveEventPicture(EventPicture eventPicture) throws Exception {
        eventPictureDao.saveOrUpdate(eventPicture);
    }

    @Override
    public Event getEventById(int eventId) throws Exception {
        return eventDao.findById(eventId);
    }

    @Override
    public void saveEventUser(EventUser eventUser) throws Exception{
        eventUserDao.saveOrUpdate(eventUser);
    }

	@Override
	public Event findEventById(int id) throws Exception {
		return eventDao.findById(id);
	}

	@Override
	public Tag findTagById(int tagId) {
		return tagDao.findById(tagId);
	}

	@Override
	public UserCollect getUserCollectByEventAndUser(int eventId, int userId) throws Exception {
		return userCollectDao.getUserCollectByEventAndUser(eventId, userId);
	}
	
	@Override
	public void saveUserCollect(UserCollect userCollect) throws Exception {
		userCollectDao.save(userCollect);
	}
	
	@Override
	public void deleteUserCollect(UserCollect userCollect) throws Exception {
		userCollectDao.delete(userCollect);
	}

	@Override
	public List<Event> getAllActivity(String keyword) {
		return eventDao.getAllActivity(keyword);
	}

	/*@Override
	public List<Tag> getTagListByTypeName(String typeName) {
		return tagDao.getTagListByTypeName(typeName);
	}*/

	@Override
	public List<TypeName> getActivityTypes(String type) {
		return typeNameDao.getActivityTypes(type);
	}

	@Override
	public List<Tag> getTagListByTypeNameId(Integer typeNameId) {
		if (typeNameId == null) {
			return tagDao.getAllTags();
		} else {
			return tagDao.getTagsByTypeNameId(typeNameId);
		}
	}
	
}
