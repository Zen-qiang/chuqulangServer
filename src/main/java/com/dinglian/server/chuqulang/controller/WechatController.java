package com.dinglian.server.chuqulang.controller;

import java.util.HashMap;
import java.util.Map;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dinglian.server.chuqulang.model.User;
import com.dinglian.server.chuqulang.service.UserService;
import com.dinglian.server.chuqulang.utils.RequestHelper;
import com.dinglian.server.chuqulang.utils.ResponseHelper;

@Controller
@RequestMapping("/api")
public class WechatController {
	
	private static final Logger logger = LoggerFactory.getLogger(WechatController.class);
	
	@Autowired
	private UserService userService;

	@ResponseBody
	@RequestMapping("/launchActivity")
	public Map<String, Object> test(){
		Map<String, Object> resultMap = new HashMap<String, Object>();
        try {
        	logger.info("=====> Start to launch activity <=====");
        	
        	Subject currentUser = SecurityUtils.getSubject();
        	User user = (User) currentUser.getSession().getAttribute(User.CURRENT_USER);
        	
          
            
            logger.info("=====> Launch activity end <=====");
            ResponseHelper.addResponseData(resultMap, RequestHelper.RESPONSE_STATUS_OK, "");
        } catch (Exception e) {
            e.printStackTrace();
            ResponseHelper.addResponseData(resultMap, RequestHelper.RESPONSE_STATUS_FAIL, e.getMessage());
        }
        return resultMap;
	}

}
