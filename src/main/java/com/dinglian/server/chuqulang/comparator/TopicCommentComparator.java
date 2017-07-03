package com.dinglian.server.chuqulang.comparator;

import java.util.Comparator;

import com.dinglian.server.chuqulang.model.TopicComment;

public class TopicCommentComparator implements Comparator<TopicComment>{

	/**
	 * 按倒序排序，最新的评论在最前面
	 */
	@Override
	public int compare(TopicComment o1, TopicComment o2) {
		if ((o1.getCreationDate().getTime() - o2.getCreationDate().getTime()) > 0) {
			return -1;
		} else if ((o1.getCreationDate().getTime() - o2.getCreationDate().getTime()) < 0) {
			return 1;
		} else {
			return 0;
		}
	}

}
