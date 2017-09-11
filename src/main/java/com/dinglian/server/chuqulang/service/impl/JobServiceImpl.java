package com.dinglian.server.chuqulang.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dinglian.server.chuqulang.dao.CoterieDao;
import com.dinglian.server.chuqulang.dao.GeneralDao;
import com.dinglian.server.chuqulang.model.Coterie;
import com.dinglian.server.chuqulang.model.Event;
import com.dinglian.server.chuqulang.model.SensitiveWord;
import com.dinglian.server.chuqulang.model.User;
import com.dinglian.server.chuqulang.model.WxAccessToken;
import com.dinglian.server.chuqulang.service.JobService;

@Service
public class JobServiceImpl implements JobService {

	@Autowired
	private GeneralDao generalDao;
	
	@Autowired
	private CoterieDao coterieDao;

	@Override
	public void changeActivityStatus(int id, String status) {
		if (status.equalsIgnoreCase(Event.STATUS_PROCESS)) {
			generalDao.changeActivityStatus(id, status, Event.STATUS_SIGNUP);
		} else if (status.equalsIgnoreCase(Event.STATUS_OVER)) {
			generalDao.changeActivityStatus(id, status, null);
		}
	}

	@Override
	public int getActivityUserCount(int id) {
		return generalDao.getActivityUserCount(id);
	}

	@Override
	public String getWxAccessToken() {
		WxAccessToken token = generalDao.findWxAccessTokenById(WxAccessToken.ACCESS_TOKEN_ID);
		if (token != null) {
			return token.getAccessToken();
		}
		return null;
	}
	
	@Override
	public List<Event> getSingnUpActivitys() {
		return generalDao.getSingnUpActivitys();
	}

	@Override
	public Event findEventById(int id) {
		return generalDao.findEventById(id);
	}

	@Override
	public User getActivityCreator(int id) {
		return generalDao.getActivityCreator(id);
	}

	@Override
	public List<User> getActivityMembers(int activityId) {
		return generalDao.getActivityMembers(activityId);
	}

	@Override
	public List<SensitiveWord> loadAllSensitiveWord() {
		return generalDao.loadAllSensitiveWord();
	}

	@Override
	public Coterie getCoterieByActivityId(int activityId) {
		return generalDao.getCoterieByActivityId(activityId);
	}

	@Override
	public boolean hasActivityProcess(int coterieId) {
		return coterieDao.hasActivityProcess(coterieId);
	}

	@Override
	public void changeCoterieStatus(int coterieId, int status) {
		generalDao.changeCoterieStatus(coterieId, status);
	}
	
}
