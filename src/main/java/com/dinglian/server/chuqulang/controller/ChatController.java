package com.dinglian.server.chuqulang.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dinglian.server.chuqulang.model.ChatRoom;
import com.dinglian.server.chuqulang.model.Event;
import com.dinglian.server.chuqulang.model.User;
import com.dinglian.server.chuqulang.service.ActivityService;
import com.dinglian.server.chuqulang.service.ChatService;
import com.dinglian.server.chuqulang.utils.NeteaseIMUtil;
import com.dinglian.server.chuqulang.utils.RequestHelper;
import com.dinglian.server.chuqulang.utils.ResponseHelper;

import net.sf.json.JSONObject;

@Controller
@RequestMapping("/chat")
public class ChatController {

	private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

	/*
	 * @Autowired private UserService userService;
	 */

	@Autowired
	private ActivityService activityService;

	@Autowired
	private ChatService ChatService;

	@Autowired
	private HttpServletResponse response;

	@ResponseBody
	@RequestMapping(value = "/chating", method = RequestMethod.POST)
	public Map<String, Object> chating(@RequestParam(name = "eventId") String eventIdStr,
			@RequestParam(name = "password") String password) {
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
	 * 
	 * @param to
	 *            接受者accid
	 * @param type
	 *            0 表示文本消息,1 表示图片，2 表示语音，3 表示视频，4 表示地理位置信息，6 表示文件，100 自定义消息类型
	 * @param body
	 *            发送内容
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/sendMessage", method = RequestMethod.POST)
	public String sendMessage(@RequestParam("to") String to, @RequestParam("type") int type,
			@RequestParam("body") String body) {
		String responseStr = "";
		try {
			logger.info("=====> Start to send message <=====");
			Subject currentUser = SecurityUtils.getSubject();
			User user = (User) currentUser.getSession().getAttribute(User.CURRENT_USER);

			responseStr = NeteaseIMUtil.getInstance().basicSendMsg(user.getAccid(), 0, to, type, body);
			
			// 保存内容到本地
		} catch (Exception e) {
			e.printStackTrace();
			responseStr = e.getMessage();
		}
		return responseStr;
	}

	/**
	 * 创建聊天室
	 * 
	 * @param eventId
	 *            活动ID
	 * @param name
	 *            聊天室名称
	 * @param announcement
	 *            聊天室公告
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/createChatRoom", method = RequestMethod.POST)
	public String createChatRoom(@RequestParam("eventId") int eventId, @RequestParam("name") String name,
			@RequestParam(name = "announcement", required = false) String announcement) {
		String responseStr = "";
		try {
			logger.info("=====> Start to create chatroom <=====");
			Subject currentUser = SecurityUtils.getSubject();
			User user = (User) currentUser.getSession().getAttribute(User.CURRENT_USER);

			Event event = activityService.findEventById(eventId);

			if (event.getChatRoom() == null) {
				responseStr = NeteaseIMUtil.getInstance().createChatroom(user.getAccid(), name, announcement, null,
						null);
				JSONObject responseObj = JSONObject.fromObject(responseStr);
				if (responseObj.getInt("code") == 200) {
					JSONObject chatRoomObj = responseObj.getJSONObject("chatroom");

					ChatRoom chatRoom = new ChatRoom();
					chatRoom.setCreator(user);
					chatRoom.setEvent(event);
					chatRoom.setCreationDate(new Date());
					chatRoom.setAnnouncement(announcement);
					chatRoom.setName(name);
					chatRoom.setValid(true);
					chatRoom.setChatRoomId(chatRoomObj.getInt("roomid"));

					// ChatService.saveChatRoom(chatRoom);
					event.setChatRoom(chatRoom);
					activityService.saveEvent(event);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			responseStr = e.getMessage();
		}
		logger.info("=====> Create chatroom end <=====");
		return responseStr;
	}

	/**
	 * 查询聊天室
	 * 
	 * @param roomid
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/getChatRoom", method = RequestMethod.POST)
	public String getChatRoom(@RequestParam("roomid") long roomid) {
		String responseStr = "";
		try {
			logger.info("=====> Start to get chatroom <=====");
			responseStr = NeteaseIMUtil.getInstance().getChatroom(roomid, true);
		} catch (Exception e) {
			e.printStackTrace();
			responseStr = e.getMessage();
		}
		logger.info("=====> Get chatroom end <=====");
		return responseStr;
	}

	/**
	 * 更新聊天室
	 * 
	 * @param roomid
	 *            聊天室ID
	 * @param name
	 *            聊天室名称
	 * @param announcement
	 *            聊天室公告
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/updateChatRoom", method = RequestMethod.POST)
	public String updateChatRoom(@RequestParam("roomid") int roomid,
			@RequestParam(name = "name", required = false) String name,
			@RequestParam(name = "announcement", required = false) String announcement) {
		String responseStr = "";
		try {
			logger.info("=====> Start to update chatroom <=====");
			responseStr = NeteaseIMUtil.getInstance().updateChatroom(roomid, name, announcement, null, null, true,
					null);
			JSONObject responseObj = JSONObject.fromObject(responseStr);
			if (responseObj.getInt("code") == 200) {
				ChatRoom chatRoom = ChatService.findChatRoomByRoomId(roomid);
				if (StringUtils.isNotBlank(name)) {
					chatRoom.setName(name);
				}
				chatRoom.setAnnouncement(announcement);
				ChatService.saveChatRoom(chatRoom);
			}
		} catch (Exception e) {
			e.printStackTrace();
			responseStr = e.getMessage();
		}
		logger.info("=====> Update chatroom end <=====");
		return responseStr;
	}

	/**
	 * 修改聊天室开/关闭状态
	 * 
	 * @param roomid
	 * @param valid
	 *            true或false，false:关闭聊天室；true:打开聊天室
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/toggleCloseChatRoom", method = RequestMethod.POST)
	public String toggleCloseChatRoom(@RequestParam("roomid") int roomid, @RequestParam("valid") boolean valid) {
		String responseStr = "";
		response.setCharacterEncoding("UTF-8");
		try {
			logger.info("=====> Start to toggle chatroom closed <=====");
			Subject currentUser = SecurityUtils.getSubject();
			User user = (User) currentUser.getSession().getAttribute(User.CURRENT_USER);

			ChatRoom chatRoom = ChatService.findChatRoomByRoomId(roomid);
			if (chatRoom != null && chatRoom.getCreator().getAccid().equalsIgnoreCase(user.getAccid())) {
				responseStr = NeteaseIMUtil.getInstance().toggleCloseStat(roomid, user.getAccid(), valid);
				JSONObject responseObj = JSONObject.fromObject(responseStr);
				if (responseObj.getInt("code") == 200) {
					chatRoom.setValid(valid);
					ChatService.saveChatRoom(chatRoom);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			responseStr = e.getMessage();
		}
		logger.info("=====> Tollge chatroom closed end <=====");
		return responseStr;
	}

	/**
	 * 请求聊天室地址
	 * 
	 * @param roomid
	 * @param clienttype
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/requestChatRoomAddr", method = RequestMethod.POST)
	public String requestChatRoomAddr(@RequestParam("roomid") int roomid,
			@RequestParam(name = "clienttype", required = false) Integer clienttype) {
		String responseStr = "";
		try {
			logger.info("=====> Start to request chatroom address <=====");
			Subject currentUser = SecurityUtils.getSubject();
			User user = (User) currentUser.getSession().getAttribute(User.CURRENT_USER);

			if (clienttype == null) {
				clienttype = 1;
			}
			responseStr = NeteaseIMUtil.getInstance().requestAddr(roomid, user.getAccid(), clienttype);
		} catch (Exception e) {
			e.printStackTrace();
			responseStr = e.getMessage();
		}
		logger.info("=====> Request chatroom address end <=====");
		return responseStr;
	}

	/**
	 * 发送聊天室信息
	 * 
	 * @param roomid
	 *            聊天室id
	 * @param msgId
	 *            客户端消息id，使用uuid等随机串，msgId相同的消息会被客户端去重
	 * @param msgType
	 *            消息类型： 0: 表示文本消息， 1: 表示图片， 2: 表示语音， 3: 表示视频， 4: 表示地理位置信息， 6:
	 *            表示文件， 10: 表示Tips消息， 100: 自定义消息类型
	 * @param resendFlag
	 *            重发消息标记，0：非重发消息，1：重发消息，如重发消息会按照msgid检查去重逻辑
	 * @param attach
	 *            消息内容，格式同消息格式示例中的body字段,长度限制4096字符
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/chatRoomSendMsg", method = RequestMethod.POST)
	public String chatRoomSendMsg(@RequestParam("roomid") int roomid, @RequestParam("msgId") String msgId,
			@RequestParam("msgType") int msgType,
			@RequestParam(name = "resendFlag", required = false) Integer resendFlag,
			@RequestParam(name = "attach", required = false) String attach) {
		String responseStr = "";
		try {
			logger.info("=====> Start to send chatroom message <=====");
			Subject currentUser = SecurityUtils.getSubject();
			User user = (User) currentUser.getSession().getAttribute(User.CURRENT_USER);

			responseStr = NeteaseIMUtil.getInstance().sendChatroomMsg(roomid, msgId, user.getAccid(), msgType,
					resendFlag, attach);
			
			// 保存内容到本地
		} catch (Exception e) {
			e.printStackTrace();
			responseStr = e.getMessage();
		}
		logger.info("=====> Send chatroom essage end <=====");
		return responseStr;
	}

	@ResponseBody
	@RequestMapping(value = "/queryMembers", method = RequestMethod.POST)
	public String queryMembers() {
		String responseStr = "";
		/*
		 * try { logger.info("=====> Start to get chatroom <====="); responseStr
		 * = NeteaseIMUtil.getInstance().getChatroom(roomid, true); } catch
		 * (Exception e) { e.printStackTrace(); responseStr = e.getMessage(); }
		 * logger.info("=====> Get chatroom end <=====");
		 */
		return responseStr;
	}

}
