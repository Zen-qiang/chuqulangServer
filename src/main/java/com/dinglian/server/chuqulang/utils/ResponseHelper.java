package com.dinglian.server.chuqulang.utils;

import java.util.Collections;
import java.util.Map;

public class ResponseHelper {

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

}
