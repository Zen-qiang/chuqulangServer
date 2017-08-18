package com.dinglian.server.chuqulang.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ResponseHelper {
	
	public static final boolean RESPONSE_SUCCESS_TRUE = true;
	public static final boolean RESPONSE_SUCCESS_FALSE = false;

	public static void addResponseData(Map<String, Object> resultMap, String status, String message) {
		resultMap.put("status", status);
		resultMap.put("message", message);
		resultMap.put("result", Collections.emptyMap());
	}

	public static void addResponseData(Map<String, Object> resultMap, String status, String message, Object result) {
		resultMap.put("status", status);
		resultMap.put("message", message);
		resultMap.put("result", result);
	}

	public static void addResponseFailData(Map<String, Object> resultMap, String message) {
		resultMap.put("success", RESPONSE_SUCCESS_FALSE);
		Map<String, Object> errorMap = new HashMap<String, Object>();
		errorMap.put("message", message);
		resultMap.put("error", errorMap);
	}
	
	public static void addResponseFailData(Map<String, Object> resultMap, String message, Object data) {
		resultMap.put("success", RESPONSE_SUCCESS_FALSE);
		Map<String, Object> errorMap = new HashMap<String, Object>();
		errorMap.put("message", message);
		resultMap.put("error", errorMap);
		resultMap.put("data", data);
	}
	
	public static void addResponseFailData(Map<String, Object> resultMap, Integer errorCode, String message) {
		Map<String, Object> errorMap = new HashMap<String, Object>();
		errorMap.put("message", message);
		errorMap.put("errorCode", errorCode);
		resultMap.put("error", errorMap);
		resultMap.put("success", RESPONSE_SUCCESS_FALSE);
	}
	
	public static void addResponseSuccessData(Map<String, Object> resultMap, Object data) {
		resultMap.put("success", RESPONSE_SUCCESS_TRUE);
		resultMap.put("data", data);
	}

	public static void addInterceptorResponseData(Map<String, Object> map, int code, String message) {
		map.put("message", message);
		map.put("code", code);
		map.put("status", RequestHelper.RESPONSE_STATUS_FAIL);
		
	}

}
