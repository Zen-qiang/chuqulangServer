package com.dinglian.server.chuqulang.base;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {

	public static void main(String[] args) {
		String mobiles = "17784651456";
		Pattern p = Pattern.compile("^1(3|4|5|7|8)\\d{9}$");  
		Matcher m = p.matcher(mobiles);  
		System.out.println(m.matches());  
	}

}
