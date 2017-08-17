package com.dinglian.server.chuqulang.comparator;

import java.util.Comparator;

import com.dinglian.server.chuqulang.model.TopicPraise;

public class TopicPraiseComparator implements Comparator<TopicPraise>{

	@Override
	public int compare(TopicPraise o1, TopicPraise o2) {
		return o2.getOrderNo() - o1.getOrderNo();
	}

}
