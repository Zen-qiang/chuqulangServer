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

import com.dinglian.server.chuqulang.model.User;
import com.dinglian.server.chuqulang.utils.NeteaseIMUtil;
import com.dinglian.server.chuqulang.utils.RequestHelper;
import com.dinglian.server.chuqulang.utils.ResponseHelper;

import net.sf.json.JSONObject;

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
    
    /**
     * 发送信息
     * @param to	接受者accid
     * @param type	0 表示文本消息,1 表示图片，2 表示语音，3 表示视频，4 表示地理位置信息，6 表示文件，100 自定义消息类型
     * @param body	发送内容
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/sendMessage", method = RequestMethod.POST)
    public String createUserID(@RequestParam("to") String to, @RequestParam("type") int type, 
    		@RequestParam("body") String body) {
//        Map<String, Object> resultMap = new HashMap<String, Object>();
    	String responseStr = "";
        try {
        	logger.info("=====> Start to send message <=====");
			Subject currentUser = SecurityUtils.getSubject();
			User user = (User) currentUser.getSession().getAttribute(User.CURRENT_USER);
			
			responseStr = NeteaseIMUtil.getInstance().basicSendMsg(user.getAccid(), 0, to, type, body);
			/*JSONObject responseObj = JSONObject.fromObject(responseStr);
			if (responseObj.getInt("code") == 200) {
			} else {
				Map<String, Object> result = new HashMap<String, Object>();
				result.put("code", responseObj.getInt("code"));
				result.put("desc", responseObj.getInt("desc"));
			}*/
        } catch (Exception e) {
            e.printStackTrace();
            responseStr = e.getMessage();
        }
        return responseStr;
    }
    
    @ResponseBody
    @RequestMapping(value = "/createChatRoom", method = RequestMethod.POST)
    public Map<String, Object> createChatRoom(@RequestParam("name") String to, @RequestParam("type") int type, 
    		@RequestParam("body") String body) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        /*try {
        	logger.info("=====> Start to send message <=====");
			Subject currentUser = SecurityUtils.getSubject();
			User user = (User) currentUser.getSession().getAttribute(User.CURRENT_USER);
			
			String response = NeteaseIMUtil.getInstance().basicSendMsg(user.getAccid(), 0, to, type, body);
			JSONObject responseObj = JSONObject.fromObject(response);
			if (responseObj.getInt("code") == 200) {
				ResponseHelper.addResponseData(resultMap, RequestHelper.RESPONSE_STATUS_OK, "");
			} else {
				Map<String, Object> result = new HashMap<String, Object>();
				result.put("code", responseObj.getInt("code"));
				result.put("desc", responseObj.getInt("desc"));
				ResponseHelper.addResponseData(resultMap, RequestHelper.RESPONSE_STATUS_FAIL, "", result);
			}
        } catch (Exception e) {
            e.printStackTrace();
            ResponseHelper.addResponseData(resultMap, RequestHelper.RESPONSE_STATUS_FAIL, e.getMessage());
        }*/
        return resultMap;
    }

}
