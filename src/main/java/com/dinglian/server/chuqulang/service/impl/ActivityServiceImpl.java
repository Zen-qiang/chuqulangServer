package com.dinglian.server.chuqulang.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dinglian.server.chuqulang.base.SearchCriteria;
import com.dinglian.server.chuqulang.dao.ActivityDao;
import com.dinglian.server.chuqulang.dao.EventDao;
import com.dinglian.server.chuqulang.dao.EventUserDao;
import com.dinglian.server.chuqulang.dao.TagDao;
import com.dinglian.server.chuqulang.dao.UserCollectDao;
import com.dinglian.server.chuqulang.model.Event;
import com.dinglian.server.chuqulang.model.EventUser;
import com.dinglian.server.chuqulang.model.Tag;
import com.dinglian.server.chuqulang.model.UserCollect;
import com.dinglian.server.chuqulang.service.ActivityService;

@Service
public class ActivityServiceImpl implements ActivityService {

    @Autowired
    private EventDao eventDao;

    @Autowired
    private EventUserDao eventUserDao;
    
    @Autowired
    private TagDao tagDao;
    
    @Autowired
    private UserCollectDao userCollectDao;
    
    @Autowired
    private ActivityDao activityDao;

    @Override
    public void saveEvent(Event event) throws Exception {
        eventDao.save(event);
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
	public void refresh(Event event) {
		activityDao.refresh(event);
	}

	@Override
	public Map<String, Object> getActivityList(SearchCriteria searchCriteria) {
		Map<String, Object> map = new HashMap<String, Object>();
		int totalCount = eventDao.getActivityTotalCount();
		List<Event> events = eventDao.getActivityList(searchCriteria);
		map.put("totalCount", totalCount);
		map.put("resultList", events);
		return map;
	}
	
	@Override
	public boolean checkFriendJoin(int eventId, int userId) {
		return activityDao.checkFriendJoin(eventId, userId);
	}

	@Override
	public Map<String, Object> getUserActivityList(SearchCriteria searchCriteria) {
		Map<String, Object> map = new HashMap<String, Object>();
		int totalCount = eventDao.getUserActivityTotalCount(searchCriteria.getUserId());
		List<Event> events = eventDao.getUserActivityList(searchCriteria);
		map.put("totalCount", totalCount);
		map.put("resultList", events);
		return map;
	}

	@Override
	public List<Tag> getTags(String tagType) {
		return tagDao.getTags(tagType);
	}
	
	@Override
	public List<Tag> getSecondLevelTags(Integer parentId) {
		if (parentId == null) {
			return tagDao.getTags(Tag.TYPE_SECOND_LEVEL);
		} else {
			return tagDao.getChildTags(parentId);
		}
	}
}
