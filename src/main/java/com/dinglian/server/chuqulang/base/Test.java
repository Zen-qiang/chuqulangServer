package com.dinglian.server.chuqulang.base;

import java.util.Calendar;
import java.util.Date;

public class Test {

	public static void main(String[] args) {

		Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR, -5);
		
		Date date = new Date();
		System.out.println(c.getTime());
		System.out.println(date);
		
		System.out.println(c.getTimeInMillis() -date.getTime());
		
	}

}
