package com.dinglian.server.chuqulang.comparator;

import java.util.Comparator;

import com.dinglian.server.chuqulang.model.EventPicture;

public class EventPictureComparator implements Comparator<EventPicture>{

	@Override
	public int compare(EventPicture o1, EventPicture o2) {
		return o1.getOrderNo() - o2.getOrderNo();
	}

}
