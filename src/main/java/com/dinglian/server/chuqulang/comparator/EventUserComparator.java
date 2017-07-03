package com.dinglian.server.chuqulang.comparator;

import java.util.Comparator;

import com.dinglian.server.chuqulang.model.EventUser;

public class EventUserComparator implements Comparator<EventUser>{

	@Override
	public int compare(EventUser o1, EventUser o2) {
		return o1.getOrderNo() - o2.getOrderNo();
	}

}
