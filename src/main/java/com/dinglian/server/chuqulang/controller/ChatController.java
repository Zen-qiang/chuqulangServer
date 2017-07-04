package com.dinglian.server.chuqulang.controller;

import java.util.HashMap;
import java.util.Map;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dinglian.server.chuqulang.utils.RequestHelper;
import com.dinglian.server.chuqulang.utils.ResponseHelper;

@Controller
@RequestMapping("/chat")
public class ChatController {
	
	private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

   /*@Autowired
    private UserService userService;*/

    @ResponseBody
    @RequestMapping(value = "/chating", method = RequestMethod.POST)
    public Map<String, Object> chating(@RequestParam(name = "eventId") String eventIdStr, @RequestParam(name = "password") String password) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        try {
        	int eventId = Integer.parseInt(eventIdStr);
            Subject currentUser = SecurityUtils.getSubject();
            if (currentUser.isAuthenticated()) {
                ResponseHelper.addResponseData(resultMap, RequestHelper.RESPONSE_STATUS_OK, "");
            } else {
            	ResponseHelper.addResponseData(resultMap, RequestHelper.RESPONSE_STATUS_FAIL, "请先登录");
            }
        } catch (Exception e) {
            e.printStackTrace();
            ResponseHelper.addResponseData(resultMap, RequestHelper.RESPONSE_STATUS_FAIL, e.getMessage());
        }

        return resultMap;
    }

}
