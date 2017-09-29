package com.dinglian.server.chuqulang.exception;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.dinglian.server.chuqulang.utils.JsonString;
import com.dinglian.server.chuqulang.utils.ResponseHelper;

@ControllerAdvice
public class RestfulExceptionHandler {
	
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public void processMethod(MethodArgumentTypeMismatchException ex,HttpServletRequest request ,HttpServletResponse response) throws IOException {
	    Map resultMap = new HashMap<>();
	    ResponseHelper.addResponseFailData(resultMap, "参数类型不正确，" + ex.getName() + "=" + ex.getValue());
	    response.setContentType("text/html;charset=utf-8");
		JsonString.writeJsonString(response, resultMap);
		response.flushBuffer();
	}
	
	@ExceptionHandler(MissingServletRequestParameterException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public void processMethod(MissingServletRequestParameterException ex,HttpServletRequest request ,HttpServletResponse response) throws IOException {
	    Map resultMap = new HashMap<>();
	    ResponseHelper.addResponseFailData(resultMap, "缺少请求参数" + ex.getParameterName() + "[" + ex.getParameterType() + "]");
	    response.setContentType("text/html;charset=utf-8");
		JsonString.writeJsonString(response, resultMap);
		response.flushBuffer();
	}

}
