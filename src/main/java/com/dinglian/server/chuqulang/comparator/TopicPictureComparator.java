package com.dinglian.server.chuqulang.comparator;

import java.util.Comparator;

import com.dinglian.server.chuqulang.model.TopicPicture;

public class TopicPictureComparator implements Comparator<TopicPicture>{

	@Override
	public int compare(TopicPicture o1, TopicPicture o2) {
		return o1.getOrderNo() - o2.getOrderNo();
	}

}
