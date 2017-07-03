package com.dinglian.server.chuqulang.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dinglian.server.chuqulang.dao.*;
import com.dinglian.server.chuqulang.model.*;
import com.dinglian.server.chuqulang.service.ActivityService;

import java.util.List;
import java.util.Map;

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
	public List<Event> getAllActivity() {
		return eventDao.getAllActivity();
	}

	@Override
	public List<Tag> getTagListByTypeName(String typeName) {
		return tagDao.getTagListByTypeName(typeName);
	}
	
}
