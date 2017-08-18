package com.dinglian.server.chuqulang.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dinglian.server.chuqulang.base.SearchCriteria;
import com.dinglian.server.chuqulang.dao.CoterieDao;
import com.dinglian.server.chuqulang.dao.GeneralDao;
import com.dinglian.server.chuqulang.dao.TopicDao;
import com.dinglian.server.chuqulang.model.Coterie;
import com.dinglian.server.chuqulang.model.CoterieGuy;
import com.dinglian.server.chuqulang.model.Topic;
import com.dinglian.server.chuqulang.model.TopicPraise;
import com.dinglian.server.chuqulang.service.DiscoverService;

@Service
public class DiscoverServiceImpl implements DiscoverService {

	@Autowired
	private CoterieDao coterieDao;

	@Autowired
	private TopicDao topicDao;
	
	@Autowired
	private GeneralDao generalDao;
	
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

	@Override
	public Map<String, Object> getCoterieList(SearchCriteria searchCriteria) {
		Map<String, Object> map = new HashMap<String, Object>();
		int totalCount = coterieDao.getCoterieTotalCount();
		List<Coterie> coteries = coterieDao.getCoterieList(searchCriteria);
		map.put("totalCount", totalCount);
		map.put("resultList", coteries);
		return map;
	}

	@Override
	public Map<String, Object> getTopicList(SearchCriteria searchCriteria) {
		Map<String, Object> map = new HashMap<String, Object>();
		int totalCount = coterieDao.getTopicTotalCount(searchCriteria.getCoterieId());
		List<Topic> topics = coterieDao.getTopicList(searchCriteria);
		map.put("totalCount", totalCount);
		map.put("resultList", topics);
		return map;
	}

	@Override
	public Map<String, Object> searchActivityOrTopic(String keyword) {
		List<Coterie> coteries = coterieDao.getCoteriesByName(keyword);
		List<Topic> topics = topicDao.getTopicsByName(keyword);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("coterieList", coteries);
		map.put("topicList", topics);
		return map;
	}

	@Override
	public void saveTopicPraise(TopicPraise topicPraise) {
		generalDao.saveTopicPraise(topicPraise);
	}

	@Override
	public void saveCoterieGuy(CoterieGuy coterieGuy) {
		generalDao.saveCoterieGuy(coterieGuy);
	}

	@Override
	public void exitCoterie(int coterieId, int userId) {
		generalDao.deleteCoterieGuy(coterieId, userId);
	}

	@Override
	public void saveCoterie(Coterie coterie) throws Exception {
		coterieDao.save(coterie);
	}

	@Override
	public List<Coterie> getMyCoteries(String dataType, int userId) {
		return coterieDao.getMyCoteries(dataType, userId);
	}

	@Override
	public Coterie getLastCoterie(int userId) {
		return coterieDao.getLastCoterie(userId);
	}

	@Override
	public List<String> getCoterieCarouselPictures() {
		return generalDao.getCoterieCarouselPictures();
	}

}
