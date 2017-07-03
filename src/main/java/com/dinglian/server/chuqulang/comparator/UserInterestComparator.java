package com.dinglian.server.chuqulang.comparator;

import java.util.Comparator;

import com.dinglian.server.chuqulang.model.UserInterest;

public class UserInterestComparator implements Comparator<UserInterest>{

	@Override
	public int compare(UserInterest o1, UserInterest o2) {
		return o1.getOrderNo() - o2.getOrderNo();
	}

}
