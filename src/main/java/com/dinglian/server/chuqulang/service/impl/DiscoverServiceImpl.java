package com.dinglian.server.chuqulang.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dinglian.server.chuqulang.dao.CoterieDao;
import com.dinglian.server.chuqulang.dao.TopicDao;
import com.dinglian.server.chuqulang.model.Coterie;
import com.dinglian.server.chuqulang.model.Topic;
import com.dinglian.server.chuqulang.service.DiscoverService;

@Service
public class DiscoverServiceImpl implements DiscoverService {

    @Autowired
    private CoterieDao coterieDao;
    
    @Autowired
    private TopicDao topicDao;
	
	@Override
	public List<Coterie> getCoterieList(int tagId, String type) throws Exception {
		return coterieDao.getCoterieList(tagId, type);
	}

	@Override
	public Coterie findCoterieById(int id) throws Exception {
		return coterieDao.findById(id);
	}

	@Override
	public void saveTopic(Topic topic) throws Exception {
		topicDao.save(topic);
	}

	@Override
	public Topic findTopicById(int id) throws Exception {
		return topicDao.findById(id);
	}


    
}
