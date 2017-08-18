package com.dinglian.server.chuqulang.interceptor;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.dinglian.server.chuqulang.base.SearchCriteria;
import com.dinglian.server.chuqulang.model.User;
import com.dinglian.server.chuqulang.service.UserService;
import com.dinglian.server.chuqulang.utils.JsonString;
import com.dinglian.server.chuqulang.utils.RequestHelper;
import com.dinglian.server.chuqulang.utils.ResponseHelper;

public class LoginInterceptor implements HandlerInterceptor {

	private static final Log logger = LogFactory.getLog(LoginInterceptor.class);

	@Autowired
	private UserService userService;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		Subject subject = SecurityUtils.getSubject();
		if (subject.isAuthenticated()) {
			return true;
		} else if (subject.isRemembered()) {
			String phoneNo = String.valueOf(subject.getPrincipal());

			SearchCriteria searchCriteria = new SearchCriteria();
			searchCriteria.setPhoneNo(phoneNo);
			User user = userService.getUser(searchCriteria);
			if (user != null) {
				subject.getSession().setAttribute(User.CURRENT_USER, user);
				return true;
			} else {
				logger.info("用户登录信息失效，请重新登录");
				ResponseHelper.addInterceptorResponseData(map, 301, "用户登录信息失效，请重新登录");
			}
		} else {
			logger.info("用户未登录，请登录。");
			ResponseHelper.addInterceptorResponseData(map, 300, "用户未登录，请登录");
		}
		response.setContentType("text/html;charset=utf-8");
		JsonString.writeJsonString(response, map);
		return false;
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
