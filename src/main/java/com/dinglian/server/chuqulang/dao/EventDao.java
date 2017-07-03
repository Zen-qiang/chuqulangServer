package com.dinglian.server.chuqulang.dao;

import java.util.List;

import com.dinglian.server.chuqulang.model.Event;

public interface EventDao extends GenericDao<Event> {

	List<Event> getAllActivity();

}