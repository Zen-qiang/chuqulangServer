package com.dinglian.server.chuqulang.interceptor;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.dinglian.server.chuqulang.model.User;
import com.dinglian.server.chuqulang.utils.RequestHelper;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class LoginInterceptor implements HandlerInterceptor{

	private static final Log logger = LogFactory.getLog(LoginInterceptor.class);
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		logger.info("=======> LoginInterceptor <======= ");
		
		Subject currentUser = SecurityUtils.getSubject();
		User user = (User) currentUser.getSession().getAttribute(User.CURRENT_USER);
		
		if (!currentUser.isAuthenticated() || user == null) {
			logger.info("用户未登录，请登录。");
//			response.sendRedirect(request.getContextPath());
			
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("status", RequestHelper.RESPONSE_STATUS_FAIL);
			map.put("message", "用户未登录，请登录");
			map.put("code", 300);
			
			JSONArray array = JSONArray.fromObject(map);
			String json = array.toString();
	        if( json.startsWith("[") ) {
	            json = json.substring(1);
	        }
	        if( json.endsWith("]") ) {
	            json = json.substring(0, json.length()-1);
	        }
	        
//	        JSONObject obj = JSONObject.fromObject(map);
	        response.setContentType("text/html;charset=utf-8");
			response.setCharacterEncoding("utf-8");
			response.getWriter().write(json);
			return false;
		}
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

}
