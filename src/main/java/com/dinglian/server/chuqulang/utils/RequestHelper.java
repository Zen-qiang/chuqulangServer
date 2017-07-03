package com.dinglian.server.chuqulang.utils;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

public class RequestHelper {

	public static final String RESPONSE_STATUS_OK = "OK";
	public static final String RESPONSE_STATUS_FAIL = "ERROR";
	
	public static String getString(HttpServletRequest request,String parameter){
		String value = request.getParameter(parameter);
		if (StringUtils.isNotBlank(value)) {
			return value;
		} else {
			return "";
		}
	}
	
	public static int getInt(HttpServletRequest request,String parameter){
		String value = request.getParameter(parameter);
		if (StringUtils.isNotBlank(value)) {
			return Integer.parseInt(value);
		} else {
			return 0;
		}
	}
	
	public static long getLong(HttpServletRequest request,String parameter){
		String value = request.getParameter(parameter);
		if (StringUtils.isNotBlank(value)) {
			return Long.parseLong(value);
		} else {
			return 0;
		}
	}

	public static boolean getBoolean(HttpServletRequest request,String parameter){
		String value = request.getParameter(parameter);
		if (StringUtils.isNotBlank(value)) {
			return Boolean.parseBoolean(value);
		} else {
			return false;
		}
	}

	public static double getDouble(HttpServletRequest request,String parameter){
		String value = request.getParameter(parameter);
		if (StringUtils.isNotBlank(value)) {
			return Double.parseDouble(value);
		} else {
			return 0;
		}
	}
	
	public static Date getDate(HttpServletRequest request,String parameter){
		String value = request.getParameter(parameter);
		if (StringUtils.isNotBlank(value)) {
			return DateUtils.parse(value, "yyyy/MM/dd");
		} else {
			return null;
		}
	}
}
