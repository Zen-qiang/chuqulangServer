package com.dinglian.server.chuqulang.utils;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

public class JsonString {
	
	public static void writeJsonString(HttpServletResponse response, Object obj){
		if( obj == null ) {
	        throw new NullPointerException("obj is null");
	    }
		try {
			JSONArray array = JSONArray.fromObject(obj);
	        String json = array.toString();
	        if( json.startsWith("[") ) {
	            json = json.substring(1);
	        }
	        if( json.endsWith("]") ) {
	            json = json.substring(0, json.length()-1);
	        }
	        response.setCharacterEncoding("utf-8");
			response.getWriter().write(json);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
