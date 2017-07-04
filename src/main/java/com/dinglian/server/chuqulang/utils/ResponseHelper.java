package com.dinglian.server.chuqulang.utils;

import java.util.HashMap;
import java.util.Map;

public class ResponseHelper {

	public static void addResponseData(Map<String, Object> resultMap, String status, String message) {
		resultMap.put("status", status);
		resultMap.put("message", message);
		resultMap.put("result", new HashMap<>());
	}

	public static void addResponseData(Map<String, Object> resultMap, String status, String message, Object result) {
		resultMap.put("status", status);
		resultMap.put("message", message);
		resultMap.put("result", result);
	}

}
