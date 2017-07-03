package com.dinglian.server.chuqulang.shiro.factory;

import java.util.LinkedHashMap;

public class FilterChainDefinitionMapBuilder {
	
	public LinkedHashMap<String, String> buildFilterChainDefinitionMap(){
		LinkedHashMap<String, String> map = new LinkedHashMap<>();
		map.put("/user/logout", "logout");
		
		map.put("/login.jsp", "anon");
//		map.put("/shiro/login", "anon");
//		map.put("/shiro/logout", "logout");
//		map.put("/user.jsp", "authc,roles[user]");
//		map.put("/admin.jsp", "authc,roles[admin]");
		map.put("/user.jsp", "authc");
		map.put("/admin.jsp", "authc");
		map.put("/list.jsp", "user");
		return map;
	}

}
